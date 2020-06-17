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
	reject_multiple_files_if_INFO_file (Ls, R),
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

reject_multiple_files_if_INFO_file([], []).
reject_multiple_files_if_INFO_file([label('INFO-Verified', _) | T], R) :- reject_multiple_files_if_INFO_file(T, R), !.
reject_multiple_files_if_INFO_file([H|T], [H|R]) :- reject_multiple_files_if_INFO_file(T, R).

