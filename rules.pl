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
	reject_multiple_files_if_INFO_file (Ls, R)
	Out =.. [submit | R].

reject_multiple_files_if_INFO_file (S1, S2) :-
	% Ask how many files changed
	gerrit:commit_stats(F),
	% CHeck if more than 1 file has changed
	F > 2,
	% Check if one file name is INFO.yaml
    gerrit:commit_delta('\\.INFO.yaml$'),
	% If anything is false, then reject with Label.
	S2 = [label('INFO file has to be only file changed', reject(O))|S1].

% =============
% INFO.yaml requires Jenkins to have given +1 
% =============
submit_filter(In, Out) :-
    In =.. [submit | Ls],
	if_INFO_file_require_jenkins_plus_1 (Ls, R)
    Out =.. [submit | R].

if_INFO_file_require_jenkins_plus_1 (S1, S2) :-
	% Ask how many files changed
	gerrit:commit_stats(F),
	% Check that only 1 file is changed
	F = 1,
	% Check if changed file name is INFO.yaml
    gerrit:commit_delta('\\.INFO.yaml$'),
	% Check that Verified is set to +1
    gerrit:commit_label(label('Verified', 1), R),
	% If anything is false, then reject with Label.
	S2 = [label('Jenkins INFO file approval required', reject(O))|S1].
