submit_filter(In, Out) :-
	In =.. [submit | Ls],
	add_non_author_approval(Ls, R),
	Out =.. [submit | R].

add_non_author_approval(S1, S2) :-
	gerrit:commit_author(A),
	gerrit:commit_label(label('Code-Review', 2), R),
	R \= A, 
	!,
	S2 = [label('Non-Author-Code-Review', ok(R)) | S1].
add_non_author_approval(S1, [label('Non-Author-Code-Review', need(_)) | S1]).

% =============
% Only allow one file to be uploaded, if file is INFO.yaml
% =============
submit_filter(In, Out) :-
	In =.. [submit | Ls],
	reject_multiple_files_if_INFO_file(Ls, R),
	Out =.. [submit | R].

reject_multiple_files_if_INFO_file(S1, S2) :-
	% set 0 to be the change owner
	gerrit:change_owner(O),
	% Ask how many files changed
	gerrit:commit_stats(ModifiedFiles, _, _),
	% Check if more than 1 file has changed
	ModifiedFiles > 1,
	% Check if one file name is INFO.yaml
	gerrit:commit_delta('\\INFO.yaml$'),
	% If you reached here, then reject with Label.
	S2 = [label('INFO-file-not-unique', reject(0))|S1].

reject_multiple_files_if_INFO_file(S1, S1).

