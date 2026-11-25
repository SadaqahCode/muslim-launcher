# Changelog

This directory contains the development history of Muslim Launcher, organized by Patch Tracking (PT).

Each PT represents a group of related changes accumulated during development. A PT may include multiple commits, pull requests, fixes, improvements, and contributions.

## Structure

Changelogs are organized into directories containing 100 PT entries each.

```text
changelog/
├── README.md
├── 1-100/
│   ├── README.md
│   ├── PT1.md
│   ├── PT2.md
│   └── ...
├── 101-200/
│   ├── README.md
│   └── ...
└── ...
```

## **PT (Patch Tracking)**

- PT numbers are sequential and never reset.
- Each PT has its own Markdown file.
- PT numbers are not tied to a specific release or calendar year.
- A PT can contain changes from multiple commits or contributors.
- A PT does not necessarily result in a release.

## **Releases**

When a PT is included in a published release, its release information may include:

- Version name
- Version code
- Git tag
- Release date

A Git release tag points to the exact commit included in the published release.

## **Contributions**

Changes from pull requests and other contributions can be included in a PT after review and integration into the repository.
