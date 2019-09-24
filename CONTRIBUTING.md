# How to Contribute

:+1: First off, thank you for contributing to Planet Lia. It's awesome people like you that make our journey worthwhile!

**New to contributing to Open Source?** 
Don't be scared by the length of this guide, we know you are already experienced enough to contribute and we really need your help! :smile: Feel free to ask questions, everyone is a beginner at first!

Following this guidelines will make it easier for the maintainers of the project to better understand your contribution, help you write a more consistent code with the project and make it much more likely that it will be accepted and merged into the project.

All members of our community are expected to follow our [Code of Conduct](CODE_OF_CONDUCT.md). Please make sure you are welcoming and friendly.

Table of content:
- [Finding Things to Work On](#finding-things-to-work-on)
- [Bug Reports](#bug-reports)
- [Feature Requests](#feature-requests)
- [Improve Documentation](#improve-documentation-closed_book)
- [Contribute Code](#contribute-code)
- [Style Guide](#style-guide)
- [Commit Messages](#commit-messages)


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

:warning: **Adding new content** - if you will be adding new content please first [open an issue](https://github.com/planet-lia/planet-lia/issues/new?labels=documentation&template=documentation_improvement.md) with the changes you have in mind so that we can discuss them and see if they align with the goals of the project.

**A few documentation pages that you can help us with:**
* [How to write replay files guide](game-utils/match-viewer/docs/writing_replay_files.md)
* We will add more soon

## Contribute Code :wrench:

***NOTE** - We are following a standard contributing workflow for GitHub open source projects. You can check this free series for more details on the workflow used below, [How to Contribute to an Open Source Project on GitHub](https://egghead.io/courses/how-to-contribute-to-an-open-source-project-on-github).*

How to contribute code:

1. **Fork** this repository
2. **Clone** your fork
    * `git clone <SSH_OR_HTTPS_LINK_TO_YOUR_FORK>` 
3. Follow changes in original repo 
    * `git remote add upstream https://github.com/planet-lia/planet-lia`
    * `git branch --set-upstream-to=upstream/master` 
4. Configure git hooks that will automatically check the quality of your commit message
    * `git config core.hooksPath .githooks`
5. Decide what to work on
    * :large_orange_diamond: **Fix existing issue** but first let others know that you are working on it by commenting on that issue
    * :large_orange_diamond: **Open a new issue** with a suggested change and wait for it to be approved
    * :large_orange_diamond: **Contribute small changes** where you don't need to create an issue before starting to code. As a rule of thumb, a small changes are the ones that introduce obvious fixes and don't introduce new functionality. Some examples might be *spelling / grammar fixes, typos, bad formatting, improving clarity in documentation, improvement to error code messages, etc.*.
6. Create a new **branch** with a name relevant to the change you are making
    * `git checkout -b <NAME_OF_YOUR_BRANCH>`
7. **Implement your changes**
    * Make sure to **write test** when appropriate
    * Make sure all **tests run successfully**
8. **Commit** your changes
    * :bangbang: Check our [commit messages guidelines](#commit-messages)
9. **Push** changes to your fork
    * `git push origin <NAME_OF_YOUR_BRANCH>`
10. Create a **pull request** to `planet-lia:master` through GitHub website
11. **Iterate** your solution until accepted
    * If maintainers request additional changes simply implement them in your local branch, push it again to master and the pull request will get updated automatically
11. **Get merged** to Planet Lia repo! :heavy_check_mark:
12. **Delete your local branch**
    * `git checkout master`
    * `git branch -D <NAME_OF_YOUR_BRANCH>`
13. To start working on new feature
    * `git pull origin master`
    * Jump back to **step 5**

Here is a great collection of useful [git tricks](https://github.com/k88hudson/git-flight-rules) to help you use git more efficiently.

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

Bonus: If you sign your commits with your GPG key, you will impress the maintainers :sunglasses:.
Checkout this [post](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work).
### Commit Message Format
Each commit message consists of a **header**, a [**body**](#body) (optional) and a [**footer**](#footer) (optional). The footer should contain a [closing reference to an issue](https://help.github.com/articles/closing-issues-via-commit-messages/) if any. The header has a special
format that includes a [**type**](#type), a [**scope**](#scope) (optional) and a [**subject**](#subject):

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

Any line of the commit message should not be longer than 100 characters! 
This allows the message to be easier to read on GitHub as well as in various git tools.

Samples: 

```
docs(match-viewer): update replay API table description
```
```
fix(cli): command line colors not displayed correctly

Specific colors are not displayed correctly. This make it hard to read the printed messages.
```

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

### Revert
***NOTE** - If you are not a maintainer you can skip this section as you will most likely never need to revert within a Pull Request.*

Use revert when a bad commit was pushed to production. 
If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the reverted commit. In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

<!-- ## Changelog Messages
Each larger part of Planet Lia platform has its own CHANGELOG.md file (or will have in the future). 
When you are contributing the code with a pull request please add a short description of your feature to related CHANGELOG. We are following the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) convention. -->

<hr/>

Thanks for contributing! :heart::heart:

Planet Lia Team
