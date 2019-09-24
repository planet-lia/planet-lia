# Commit Message Guidelines

*Our commit message style is based and adopted from [Angular commit message guidelines](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines). 
Parts of the following description are copied directly from Angular guidelines with added occasional changes.*

We have very precise rules over how our git commit messages can be formatted.  This leads to **more
readable messages** that are easy to follow when looking through the **project history**.

Bonus: If you sign your commits with your GPG key, you will impress the maintainers :sunglasses:.
Checkout this [post](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work).

## Commit Message Format
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

## Verify Your Commit Message

Add a commit message validation hook that automatically checks your commit message format when you try to commit your changes. Run the command below and make sure all scripts within `.githooks` directory are executable.
```
git config core.hooksPath .githooks
``` 
 If you really need to omit triggering hooks use flag `--no-verify` flag with `git commit` command.