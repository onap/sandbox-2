% =============
% Only allow one file to be uploaded, if file is INFO.yaml
% =============
submit_filter(In, Out) :-
	In =.. [submit | Ls],
	reject_multiple_files_if_INFO_file(Ls, R),
	!,
	Out =.. [submit | R].

reject_multiple_files_if_INFO_file(S1, S2) :-
	% Ask how many files changed
	gerrit:commit_stats(F, _, _),
	% Check if more than 1 file has changed
	F > 1,
	% Check if one file name is INFO.yaml
	gerrit:commit_delta('INFO.yaml'),
	% If anything is false, then reject with Label.
	S2 = [label('INFO-file-has-to-be-only-file-changed', reject(user(ID)))|S1].
