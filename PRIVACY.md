# AshRoute Privacy Policy

Effective date: October 10, 2026

AshRoute is an open-source Android proxy client maintained at
https://github.com/yjd953/AshRoute. This policy describes the behavior of the
official build published by the AshRoute maintainer.

## Publisher and contact

AshRoute is published by the verified organization identified on its Google
Play store listing. The app package is `io.github.yjd953.ashroute`.

Privacy questions and requests may be submitted publicly or privately through
the contact options at:

https://github.com/yjd953/AshRoute/issues

## Summary

AshRoute has no account system, advertising, analytics, telemetry, or
developer-operated backend. The AshRoute maintainer does not collect, sell,
or share personal data through the app.

AshRoute creates a local VPN interface and sends traffic to a proxy or VPN
endpoint configured by the user. AshRoute does not provide that endpoint.
The endpoint operator and destination services may process network traffic
under their own terms and privacy policies.

## Data processed on the device

AshRoute processes and stores the following data locally when a user uses
the corresponding feature:

- proxy profiles, subscription URLs, credentials, certificates, routing
  rules, DNS settings, and app preferences;
- the list of launchable applications Android makes visible to AshRoute,
  solely to let the user configure per-app routing;
- QR-code images or camera frames used to import a configuration;
- files, clipboard text, or URLs the user explicitly chooses to import;
- local connection and diagnostic logs.

This data is not sent to the AshRoute maintainer. A user may explicitly
export a configuration or share a diagnostic log; the user controls the
destination and should remove secrets before sharing.

## Network activity

Depending on the features selected by the user, AshRoute may connect to:

- proxy servers, VPN endpoints, subscription URLs, DNS resolvers, and test
  URLs configured or selected by the user;
- GitHub to check AshRoute releases and obtain open-source geo data;
- public connectivity or IP-information endpoints used for connection tests.

Requests to these third-party services expose ordinary network metadata,
such as the requesting IP address, to those services. Their privacy policies
apply. AshRoute does not add analytics identifiers to these requests.

## VPN traffic

When VPN mode is active, Android sends selected device traffic through
AshRoute's local VPN interface. AshRoute forwards that traffic according to
the user's configuration. Traffic content is not retained or sent to the
AshRoute maintainer.

Users are responsible for choosing a trustworthy endpoint and a securely
encrypted protocol. An endpoint operator may be able to observe connection
metadata or unencrypted content. AshRoute cannot control or audit
user-provided endpoints.

## Permissions

AshRoute may request:

- VPN access to create the device-level tunnel;
- standard app visibility for per-app routing; AshRoute does not request
  Android's broad `QUERY_ALL_PACKAGES` permission;
- camera access for QR-code scanning;
- notification access for connection status and background work;
- startup access to reconnect when the user enables that option;
- access to user-selected files for import and export.

AshRoute does not use these permissions for advertising or profiling.

## Security

The Google Play build rejects VPN profiles whose connection to the configured
tunnel endpoint cannot be established as encrypted. App-managed web requests
in the Google Play build use encrypted transport, except for loopback
communication contained on the device.

Users remain responsible for the security of endpoints, DNS resolvers,
subscription providers, and configurations they select. Do not import or
share a configuration from an untrusted source.

## Retention and deletion

Data remains on the device until the user deletes it, clears AshRoute's app
data, or uninstalls the app. Data already sent to a user-selected third party
is governed by that party.

## Children

AshRoute is a technical network tool and is not directed to children under
13. The maintainer does not knowingly collect children's personal data.

## Changes

Material changes will be published in this repository before or with the
corresponding app release.

This policy does not override the terms of the GNU General Public License or
the policies of a proxy provider, VPN endpoint operator, app store, or other
third-party service selected by the user.
