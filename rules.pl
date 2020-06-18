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

%filter to require all projects to have a code-reviewer other than the owner
submit_filter(In, Out) :-
    %unpack the submit rule into a list of code reviews
    In =.. [submit | Ls],
    %add the non-owner code review requiremet
    reject_self_review(Ls, R),
    %pack the list back up and return it (kinda)
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
    %if there isn't a +2 from someone else (above rule), and there is a +2 from the owner, reject with a self-reviewed label
    S2 = [label('Self-Reviewed', reject(O))|S1].
%if the above two rules didn't make it to the ! predicate, there aren't any +2s so let the default rules through unfiltered
reject_self_review(S1, S1).
