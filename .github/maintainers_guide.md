# Maintainers Guide

Maintainers are the face of this project and are integral part of making the community friendly, helpful and accepting.
Make sure to be friendly, patient, helpful as well as consistent and professional. 
Always follow our [Code of Conduct](../CODE_OF_CONDUCT.md).

## Development Workflow

### Branches
There are two types of branches that we use:

* `master`: code that is in use in production or will be deployed to production shortly 
* `<feature-branch>`: a short lived branch containing incomplete feature/bug-fix work

`<feature-branch>` rules:
* has work describe in a approved issue(s)
* has a short life cycle before it gets merged to master
* it is merged to `master` branch via PR
* when merging PRs to `master` consider **squashing** commits if there are many of them or if their quality is bad
* after it is merged, the branch gets deleted
* normally not more than 1 developer should work on one `<feature-branch>`, if there are more developers working on it consider breaking it into more branches

### Project Boards
To manage Planet Lia development we use [Github Project boards](https://github.com/planet-lia/planet-lia/projects).

### Issues
We use the following types of issues:

* **Epic:** contains a broad requirements for a larger change. Before we start to work on it is broken down into atomic issues. To mark an Epic issue use `Epic` label.
* **Atomic**: normal issue that holds a small change with all necessary data for it to be implemented

**Close an issues** when it is:
* completed
* rejected (label `wontfix`)
* postponed (label `postponed`) - will be reopened when time comes
* invalid (label `invalid`)
* duplicate (label `duplicate`)

### Labels
We use two types of labels.

Labels defining **scope**:
* `web`
* `backend`
* `cli`
* `game-utils`
* `game`
* `infra`

Labels defining **type** of work:
* `bug`: *(atuomatic via Bug report template)* - something isn't working
* `discussion` - in discussion, can't move forward
* `documentation` - improvements or additions to documentation
* `duplicate` - this issue or pull request already exists
* `enhancement`: *(atuomatic via Feature request template)* - new feature or request
* `Epic`
* `good first issue` - good for newcomers
* `help wanted` - extra attention is needed
* `invalid` - this issue doesn't seem valid or relevant
* `postponed` - not a priority but will do in the future
* `question`: *(automatic via Question template)* - project related questions, usually from newcomers 
* `refactor` - code works but needs refactoring
* `test` - writing tests or testing functionalities
* `research` - researching opportunities
* `wontfix` - this will not be worked on


## Commit Messages

Please follow our [commit message guidelines](../CONTRIBUTING.md#commit-messages).