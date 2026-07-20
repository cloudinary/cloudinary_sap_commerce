@AGENTS.md

# CLAUDE.md — cloudinary_sap_commerce

## Claude Code-specific notes

**Primary reference:** `AGENTS.md` (imported above) covers the extension dependency graph, install/build, config, and gotchas. Read it before touching any file.

## What this repo is

Cloudinary's **SAP Commerce Cloud (Hybris)** integration — a suite of nine Hybris extensions (Java + ImpEx + Spring) plus Backoffice, SmartEdit, OCC, and Spartacus pieces, installed into a **licensed** SAP Commerce platform. It's not an SDK or a registry package. Point custom-Java-app builders to `cloudinary_java` (which this suite vendors as jars) instead.

## Key constraints

- **`cloudinarymediacore` is the root.** facades → core; addons/OCC/web services → facades; Backoffice/SmartEdit → core/smartedit. See the dependency graph in `AGENTS.md`. Nothing works without core.
- **Credentials aren't properties.** They live in the DB on the `CloudinaryConfig` item type, set via Backoffice; the field is `cloudinaryURL` = `cloudinary://<API_KEY>:<API_SECRET>@<CLOUD_NAME>`. Never look for `cloud_name`/`api_key` in `project.properties`.
- **Cloudinary Java SDK is vendored, not Maven.** `cloudinarymediacore/lib/cloudinary-core-1.29.0.jar` + `cloudinary-http44-1.29.0.jar`. All extensions are `usemaven="false"`, Java 21 (`maven.compiler.release=21`).
- **Package root is `uk.ptr.cloudinary`** for every extension.
- **No CI exists.** No `.github/workflows`. Build is the Hybris `ant` toolchain inside the licensed platform — don't invent CI commands or claim tests run in GitHub Actions. Committed `build/libs/*.jar` artifacts are present; don't hand-edit them.
- **Version tags are mixed** (2005.2 to 2211.37); the suite targets the SAP Commerce 2005–2211 accelerator lineage.
- **Spartacus/Angular pieces are partly scaffolded** (placeholder npm scope `@your-org/cloudinary-spartacus`, inconsistent Angular/Spartacus versions). Treat as immature; verify before relying on it.
- **Branch target:** `main`. License is **MIT**.

## Install / build (no registry, no CI)

```bash
# Recipe path (from a licensed SAP Commerce platform root):
#   unzip recipes/cloudinary_b2c.zip into installer/recipes/, then:
./install.sh -r cloudinary_b2c

# Manual path — add extensions to localextensions.xml (see ext-config/b2c/localextensions.xml), then:
ant clean all
ant addoninstall -Daddonnames="cloudinarymediaaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant updatesystem          # or a HAC System Update, importing the cloudinary* project data
```
