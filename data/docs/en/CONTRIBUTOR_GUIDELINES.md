# Contributor Guidelines (CONTRIBUTOR_GUIDELINES.md)

This document explains the procedures and requirements for contributing to Muslim Launcher.

It complements the [Developer Contract](./DEVELOPER_CONTRACT.md), which establishes the project's philosophy, values, design principles, and boundaries.

Every contributor is expected to understand both documents before submitting a contribution.

---

## 1. Understanding the Developer Contract

The Developer Contract serves as the foundation for decision-making in Muslim Launcher development. It is not merely a reference document; its principles and boundaries must be respected in every contribution.

Before contributing:

- Read the Developer Contract in its entirety.
- Understand the project's identity, purpose, and core values.
- Understand its design principles and non-negotiable boundaries.
- Consider how your proposed changes may affect the project's philosophy and direction.

A proposal that conflicts with the Developer Contract cannot be accepted simply because it is technically feasible.

If any part of the Developer Contract is unclear or contradictory, discuss it before proceeding with implementation.

## 2. Issues and Feature Requests

Before creating a new issue, check existing issues to avoid duplicate reports or proposals.

### Bug Reports

A bug report should include:

- A description of the problem.
- Steps to reproduce the issue.
- The expected behavior.
- The actual behavior.
- Relevant environment information, such as the application version and Android version.
- Screenshots or logs, if they help explain the problem.

Describe the issue specifically. Avoid reports that only state a conclusion without providing information that can help with investigation.

### Feature Requests

A feature proposal should explain:

- The problem or need it aims to address.
- Why the feature is relevant to Muslim Launcher's purpose.
- An outline of the proposed behavior or solution.
- Its potential impact on user experience, privacy, performance, and application complexity.

Not every feature request needs to be implemented. A proposal may be rejected if it does not align with the project's purpose, principles, or boundaries.

Discussion and approval of a feature proposal should ideally take place before implementation begins.

## 3. Pull Request Requirements

Pull Requests (PRs) are used to propose code or documentation changes for review before they are merged into the project.

Every PR must:

- Have a clear purpose and well-defined scope.
- Explain the problem being addressed or the reason for the change.
- Describe the implementation approach.
- Include information about the tests performed.
- Include screenshots or recordings if the change affects the user interface or interactions.
- Avoid mixing unrelated changes.
- Exclude build artifacts, credentials, secrets, and personal data.

### PR Principles

- A PR should focus on one primary objective.
- Avoid changes that are unnecessary to solve the stated problem.
- Do not include unrelated refactoring without a clear justification.
- Do not change existing behavior without explaining why.
- Do not assume that an implementation completed locally is automatically ready to be merged.

If a change is still under discussion or not ready for review, use a Draft Pull Request.

## 4. Coding Conventions

Code should be easy to read, understand, test, and maintain.

Contributors are expected to:

- Follow the conventions of the languages and frameworks used in the project.
- Maintain consistency with the existing code structure and patterns.
- Use clear and meaningful names.
- Avoid unnecessary code duplication.
- Avoid abstractions and generalizations that do not address an actual need.
- Handle errors and unexpected conditions appropriately.
- Remove unused code, outdated comments, and debug statements before submitting a PR.

Do not introduce a new architectural pattern simply because it is popular or considered more sophisticated.

Every architectural change must have a clear technical justification and account for long-term complexity.

## 5. Commit Conventions

Commit messages should describe the changes briefly and specifically.

Use the following format:

`<type>: <description>`

Recommended commit types:

| Type       | Usage                                                  |
| ---------- | ------------------------------------------------------ |
| `feat`     | Adding a feature or capability                         |
| `fix`      | Fixing a bug                                           |
| `refactor` | Restructuring code without changing behavior           |
| `docs`     | Updating documentation                                 |
| `style`    | Formatting or code-style changes without logic changes |
| `test`     | Adding or improving tests                              |
| `build`    | Changing build configuration or dependencies           |
| `chore`    | Other project maintenance tasks                        |

Examples:

- `feat: add app search`
- `fix: resolve gesture detection issue`
- `refactor: simplify drawer state handling`
- `docs: update developer contract`
- `test: add launcher navigation tests`

Use English for commit messages to maintain consistency across the project's development history.

Avoid vague messages that do not explain the changes, such as `update`, `changes`, `fix bug`, or `misc`.

## 6. Dependency Justification

Every new dependency adds something that must be maintained, updated, and trusted.

Therefore, dependencies must not be introduced without a clear need and justification.

Before proposing a new dependency, consider:

- Can the requirement be met using built-in Android capabilities or dependencies already available in the project?
- Is the dependency genuinely necessary?
- What impact will it have on application size, performance, and resource consumption?
- Does it require permissions, network access, or data collection that are not necessary?
- What is its maintenance status, license, and security track record?
- Does it introduce complexity that is proportional to its benefits?

Every PR that introduces a new dependency must explain why it was selected and why it is needed.

Dependencies without sufficient justification may be rejected.

## 7. Testing Requirements

Every contributor is responsible for ensuring that their proposed changes have been adequately tested.

Testing should be appropriate to the type and impact of the change.

Before submitting a PR:

- Ensure that the project builds successfully.
- Run relevant tests.
- Recheck the behavior affected by the changes.
- Ensure that existing functionality has not regressed.
- Check logs for errors or warnings introduced by the changes.
- Ensure that the changes do not introduce new performance or stability issues.

For changes affecting launcher interactions, test on relevant Android devices or emulators.

If certain tests cannot be performed, disclose the limitations clearly in the PR.

Do not claim that a change has been tested if the tests have not actually been performed.

## 8. Code Review and Approval

Every PR must go through a review process before being merged.

The review process aims to ensure that changes:

- Solve the intended problem.
- Align with the Developer Contract.
- Meet code quality and consistency standards.
- Do not introduce unnecessary complexity.
- Do not create privacy, security, performance, or stability issues.
- Have been adequately tested.

Reviewers may request clarification, implementation changes, or additional testing.

Contributors are expected to respond constructively to review feedback and update their PRs when necessary.

Final PR approval is at the discretion of the project maintainer. Submitting a PR or participating in a discussion does not guarantee that a proposed change will be accepted.

The maintainer reserves the right to reject, request revisions to, or close PRs that do not align with the project's needs and direction.

## 9. Proposing Changes to the Developer Contract

The Developer Contract establishes the project's foundational principles. Changes to it must be treated more seriously than ordinary documentation or implementation changes.

To propose a change to the Developer Contract:

1. Create a dedicated issue explaining which section you want to change.
2. Reference the specific section or principle affected.
3. Explain why the change is necessary.
4. Describe its potential impact on the project's philosophy, values, design principles, and boundaries.
5. Provide proposed wording or replacement principles, if possible.
6. Wait for discussion and a decision from the maintainer before submitting changes to the document.

Changes to the Developer Contract must not be slipped into a feature or refactoring PR as an unrelated minor modification.

Proposals that weaken the project's core principles require particular scrutiny and should not be accepted merely for implementation convenience.

Every approved change must be reflected consistently in the documentation and subsequent development direction.

---

## Closing

Contributing to Muslim Launcher is not simply about adding code or resolving issues. Every change also helps shape the project's direction and character.

We value contributions that address real problems, preserve simplicity, respect users, and remain within the established boundaries.

**The quality of a contribution is not measured by how much code it adds, but by the value and responsibility behind the change.**
