# How to Contribute

:+1: First off, thank you for contributing to Planet Lia. It's awesome people like you that make our journey worthwhile!

**New to contributing to Open Source?** You can check this free series, [How to Contribute to an Open Source Project on GitHub](https://egghead.io/courses/how-to-contribute-to-an-open-source-project-on-github). Feel free to ask for help, everyone is a beginner at first!

Following this guidelines will make it easier for the maintainers of the project to better understand your contribution, help you write a more consistent code with the project and make it much more likely that it will be accepted and merged into the project.

All members of our community are expected to follow our [Code of Conduct](CODE_OF_CONDUCT.md). Please make sure you are welcoming and friendly.


## Finding Things to Work On
The first place to start is to search through the issues in the repository.
Search for issues labeld with [good first issue](https://github.com/planet-lia/planet-lia/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22) and [help wanted](https://github.com/planet-lia/planet-lia/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22).
Find something that you think you can complete and comment below the issue that you would like to work on it. 
Once you get a green light you are ready [to dig in](#join-the-development).

Of course, feel free to submit a new issue if you think something needs to be added or fixed.



## Bug Report :bug: or Feature Request :star2:?
1. #### Bug Reports

    :warning: **Security issue** - if you find a security vulnerability, do NOT open an issue. Email support@planetlia.com instead. This will prevent making the vulnerability public where someone might abuse it.

    If you find a bug in our code you can [submit a bug report](https://github.com/planet-lia/planet-lia/issues/new?labels=bug&template=bug_report.md). Please follow the template guidelines and provide as many details as possible.

2. #### Feature Requests

    Here you can [submit a feature request](https://github.com/planet-lia/planet-lia/issues/new?labels=enhancement&template=feature_request.md). Please follow the template guidelines and provide as many details as possible.

    If a feature is related to a specific game and not directly to the Planet Lia platform, please use a section in our [Forum](https://www.reddit.com/r/liagame/) dedicated to that game.

    Note that some feature request might be out of scope or not aligned with the goals of the project and might not be accepted. Even if that happens we are very happy that you submitted your request and don't be discouraged to open another one when new idea pops into your mind.

## Improve Documentation :closed_book:

We have large amounts of documentation that take a lot of time and effort for us to write and curate. Any help with it is greatly appreciated.
 
**Some ideas where you can help:**
* fixing typos, spelling & grammar
* fixing inconsistencies throughout the code
* fixing outdated information
* adding new content in form of context clarifications, adding tutorials or new documentation sections 

:warning: **Adding new content** - if you will be adding new content please first open a [feature request](https://github.com/planet-lia/planet-lia/issues/new?labels=enhancement&template=feature_request.md) with the changes you have in mind so that we can discuss them and see if they align with the goals of the project.

**A few documentation pages that you can help us with:**
* [How to write replay files guide](game-utils/match-viewer/docs/writing_replay_files.md)
* We will add more soon

## Join the Development :wrench:

:warning: **Fixing existing issues** - if you are planning to tackle an existing issue please do write a comment on that issue so that there will not be multiple people working on it at the same time

1. #### Trivial Changes

    To contribute small changes you don't need to create an issue but you can directly submit your pull request. As a rule of thumb, a small changes are the ones that introduce obvious fixes and don't introduce new functionality. Some examples might be:
    * Spelling / grammar fixes
    * Typo corrections, bad formatting fixes
    * Improving clarity in documentation
    * Improvement to error code messages
    * Configuration changes

    When you **commit** your changes, make sure to follow our [commit messages guidelines](#commit-messages). :bangbang: 

2. #### Bigger changes

    Follow the workflow below:
    * **Open an issue** with a suggested change and wait for it to be **approved**
    * **Fork and clone** this repository
    * Create a **branch**
    * **Implement your changes**
    * Make sure to include **tests when appropriate**
    * Make sure all **tests run successfully**
    * Add a short description of the change in [corresponding CHANGELOG](#changelog-messages)
    * **Commit** your changes, make sure to follow our [commit messages guidelines](#commit-messages) :bangbang: 
    * **Push** changes to your fork
    * Create a **pull request** to `planet-lia:master`
    * **Iterate** until accepted
    * **Get merged** to Planet Lia repo! :heavy_check_mark:

    Don't worry if maintainers request that you improve your pull request.
    Sometimes this is necessary in order to keep the code standard high.

    **Before you start coding** - make sure to first open an issue with the changes that you want to introduce and only start working on your pull request when the changes are approved.

    Here is a great collection of useful [git tricks](https://github.com/k88hudson/git-flight-rules) if sometimes you don't know how to do something.

### What We Don't Accept
    
This are a few reasons why your pull request might get rejected:
* Cosmetic style changes to existing code which sole purpose is to make the code look more beautiful and don't improve any functionality. Code style tastes differ from person to person, stick with a style that the project is already using.
* Pull requests that don't follow the [style guide](#style-guide) of the project.
* Pull requests that are not trivial and don't refer to any previously opened issue and/or that the issue they refer to was rejected.


## Style Guide
Each sub-project in Planet Lia platform has its own style depending on the language and the frameworks used. 
Before you start writing code skim through other parts of the project that you will work on and use the style that you find there. 

A few important style conventions that you need to consider:
* naming variables (`variable_name` vs. `variableName`)
* naming files (`MyWorker.xy` vs. `myWorker.xy` vs. `my_worker.xy`)
* spacing (2 vs. 4 spaces vs. tabs)
* brackets and formating (curly braces positions, line wrapping)
* writing test when appropriate
* adding comments to your code
* documenting your changes in project specific docs (usually README.md within the project)
* **Golang** - before commiting use `go fmt` command to format your code
* **Markdown** - put every sentence in a new line as it helps a ton when reviewing changes with git diff

## Commit Messages
*Our commit message style is based and adopted from [Angular commit message guidelines](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines). 
Parts of the following description are copied directly from Angular guidelines with added occasional changes.*

We have very precise rules over how our git commit messages can be formatted.  This leads to **more
readable messages** that are easy to follow when looking through the **project history**.

:warning: Add a commit message validation hook that checks your commit message format when you try to commit your changes. Run `git config core.hooksPath .githooks` within your cloned repository and make sure all scripts within `.githooks` directory are executable. If you really need to omit triggering hooks use flag `--no-verify` flag with `git commit` command.

Bonus: If you sign your commits with your GPG key, you will impress the maintainers :sunglasses:.
Checkout this [post](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work).
### Commit Message Format
Each commit message consists of a **header**, a **body** and a **footer**.  The header has a special
format that includes a **type**, a **scope** and a **subject**:

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

The **header** is mandatory and the **scope** of the header is optional.

Any line of the commit message cannot be longer 100 characters! 
This allows the message to be easier to read on GitHub as well as in various git tools.

The footer should contain a [closing reference to an issue](https://help.github.com/articles/closing-issues-via-commit-messages/) if any.

Samples: 

```
docs(match-viewer): update replay API table description
```
```
fix(cli): command line colors not displayed correctly

Specific colors are not displayed correctly. This make it hard to read the printed messages.
```

### Revert
***NOTE** - If you are not a maintainer you can skip this section as you will most likely never need to revert within a Pull Request.*

Use revert when a bad commit was pushed to production. 
If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the reverted commit. In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

### Type
Must be one of the following:

* **build**: Changes that affect the build system or external dependencies
* **ci**: Changes to our CI configuration files and scripts
* **infra**: Changes to infrastructure and deployment configuration
* **docs**: Development documentation changes
* **feat**: A new feature
* **fix**: A bug fix
* **perf**: A code change that improves performance
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
* **test**: Adding missing tests or correcting existing tests
* **chore**: Small changes (eg. updating changelog, upgrading dependency, ..)

### Scope
The scope should be the name of the package affected.

The following is the list of supported scopes:
* **web-docs**
* **web-main**
* **web-local**
* **docs**
* **backend**
* **cli**
* **match-viewer**
* **game-template**
* **game-\<game-name\>**
* we will add more as we go

### Subject
The subject contains a succinct description of the change:

* use the imperative, present tense: "change" not "changed" nor "changes"
* don't capitalize the first letter
* no dot (.) at the end
* limit the first line to 72 characters or less

### Body
Just as in the **subject**, use the imperative, present tense: "change" not "changed" nor "changes".
The body should include the motivation for the change and contrast this with previous behavior.

### Footer
The footer should contain any information about **Breaking Changes** and is also the place to reference GitHub issues that this commit **Closes**.

**Breaking Changes** should start with the word `BREAKING CHANGE:` with a space or two newlines. The rest of the commit message is then used for this.

Example of a breaking change and closing an issue.

```
feat(backend): rename achievement field in user info endpoint

Previous achievement field was called "userAchievements" this has been renamed to 
simply "achievements".

BREAKING CHANGE: User achievements field rename in user info endpoint.
Closes #105"
```

## Changelog Messages
Each larger part of Planet Lia platform has its own CHANGELOG.md file. 
When you are contributing the code with a pull request please add a short description of your feature to related CHANGELOG within your PR. We are following the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) convention.

<hr/>

Thanks for contributing! :heart::heart:

Planet Lia Team
