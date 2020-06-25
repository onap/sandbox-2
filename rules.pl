%filter to require all projects to have a code-reviewer other than the owner
submit_filter(In, Out) :-
    In =.. [submit | Ls],
    %add the non-owner code review requiremet
    reject_self_review(Ls, R1),
    %Reject if multiple files and one is INFO.yaml
    ensure_info_file_is_only_file(R1, R2),
    %Reject if not INFO file has been verified by Jenkins
    if_info_file_require_jenkins_plus_1(R2, R),
    Out =.. [submit | R].

reject_self_review(S1, S2) :-
    %set O to be the change owner
    gerrit:change_owner(O),
    %find a +2 code review, if it exists, and set R to be the reviewer
    gerrit:commit_label(label('Code-Review', 2), R),
    %if there is a +2 review from someone other than the owner, then the filter has no work to do, assign S2 to S1
    R \= O, !,
    %the cut (!) predicate prevents further rules from being consulted
    S2 = S1.

reject_self_review(S1, S2) :-
    %set O to be the change owner
    gerrit:change_owner(O),
    %find a +2 code review, if it exists, and set R to be the reviewer - comment sign was missing
    gerrit:commit_label(label('Code-Review', 2), R),
    R = O, !,
    %if there is not a +2 from someone else (above rule), and there is a +2 from the owner, reject with a self-reviewed label
    S2 = [label('Self-Reviewed', reject(O))|S1].

% if the above two rules did not make it to the ! predicate, there are not any +2s so let the default rules through unfiltered
reject_self_review(S1, S1).


% =============
% Only allow one file to be uploaded, if file is INFO.yaml
% =============
ensure_info_file_is_only_file(S1, S2) :-
    %set O to be the change owner
    gerrit:change_owner(O),
    % Ask how many files changed
    gerrit:commit_stats(ModifiedFiles, _, _),
    % Check if more than 1 file has changed
    ModifiedFiles > 1,
    % Check if one file name is INFO.yaml
    gerrit:commit_delta('INFO.yaml'),
    % If above two statements are true, give the cut (!) predicate.
    !,
    % If you reached here, then reject with Label.
    S2 = [label('INFO-file-not-alone', reject(O))|S1].

ensure_info_file_is_only_file(S1, S1).


% Define who is the special Jenkins user
jenkins_user(459).   % onap_jobbuilder
jenkins_user(3).     % ecomp_jobbuilder
jenkins_user(4937).  % releng-lf-jobbuilder

if_INFO_file_require_jenkins_plus_1(S1, S2)
    S2 = [label('Verified-by-Jenkins', need(_))|S1].

