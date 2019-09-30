# Official Planet Lia Website

Currently we are serving the placeholder landing page.

Source of [planetlia.com](https://planetlia.com).

The master branch gets automatically build and deployed to our webserver hosted on [Netlify](https://www.netlify.com/).

[![Netlify Status](https://api.netlify.com/api/v1/badges/9191587a-240f-43d6-9b25-f0ecc9be1fdf/deploy-status)](https://app.netlify.com/sites/planetlia/deploys)

All pull-requests are build as well and our GitHub bot publishes deploy previews on the associated pull-request.
## Dependencies
* Node.js (v12+)
* npm (v6.10+)
* GNU Make

## Development
1. `git clone REPO_URL`
2. `cd web/main/`
3. `make install`
4. `make dev`
5. Visit http://localhost:3000/

For production builds run:
```bash
make build
```