submit_filter(In, Out) :-
    In =.. [submit | Ls],
    add_non_author_approval(Ls, R),
    Out =.. [submit | R].

add_non_author_approval(S1, S2) :-
    gerrit:commit_author(A),
    gerrit:commit_label(label('Code-Review', 2), R),
    R \= A, !,
    S2 = [label('Non-Author-Code-Review', ok(R)) | S1].
add_non_author_approval(S1, [label('Non-Author-Code-Review', need(_)) | S1]).

% =============
% Only allow one file to be uploaded, if file is INFO.yaml
% =============
submit_filter(In, Out) :-
	In =.. [submit | Ls],
	reject_multiple_files_if_INFO_file(Ls, R),
	!,
	Out =.. [submit | R].

% =============
% Remove special INFO.yaml label (since not info.yaml)
% =============
submit_filter(In, Out) :-
	In =.. [submit | Ls],
	remove_verified_info_yaml(Ls, R),
	Out =.. [submit | R].

remove_verified_info_yaml([], []).
remove_verified_info_yaml([label('INFO-Verified', _) | T], R) :- remove_verified_info_yaml(T, R), !.
remove_verified_info_yaml([H|T], [H|R]) :- remove_verified_info_yaml(T, R).

reject_multiple_files_if_INFO_file(S1, S2) :-
	% Ask how many files changed
	gerrit:commit_stats(F, _, _),
	% Check if more than 1 file has changed
	F > 1,
	% Check if one file name is INFO.yaml
	gerrit:commit_delta('^INFO.yaml$'),
	% If anything is false, then reject with Label.
	S2 = [label('INFO file has to be only file changed', reject(O))|S1].

submit_rule(submit(CR, V, RV)) :-
  needs_release_verified,
  !,
  gerrit:max_with_block(-1, 1, 'Release-Verified', RV),
  gerrit:max_with_block(-2, 2, 'Code-Review', CR),
  gerrit:max_with_block(-1, 1, 'Verified', V).

submit_rule(submit(CR, V, IV)) :-
  needs_info_verified,
  !,
  gerrit:max_with_block(-1, 1, 'INFO-Verified', IV),
  gerrit:max_with_block(-2, 2, 'Code-Review', CR),
  gerrit:max_with_block(-1, 1, 'Verified', V).

submit_rule(submit(CR, V)) :-
  gerrit:max_with_block(-2, 2, 'Code-Review', CR),
  gerrit:max_with_block(-1, 1, 'Verified', V).

needs_release_verified :- gerrit:commit_delta('^releases/'), !.
needs_info_verified :- gerrit:commit_delta('INFO.yaml'), !.

