# NVA orcid client

A REST service to integrate with ORCID.org web services.

## What it does

1. Creates and stores API token for a user with an ORCID for later use by NVA (such as updating and downloading reported results in ORCID API).

## Service diagram

TBD

## Development

1. Install Java version listed in [buildspec.yaml](buildspec.yaml)
2. Clone/Fork the repository
3. Build with Gradle

## Deployment

The software is a self-contained AWS serverless application and can be deployed using NVA Infrastructure or as a standalone via AWS' many other alternatives.

## Contributing code

The build is configured to maintain code standards. Contribute via PR with description and context for change.
