Smuggler Docs
=============

This branch is where we keep all the project docs. The idea is to author
markdown and SVG, then generate a static site out of the sources and let
GitHub Pages automatically publish it for us.

About GitHub Pages
------------------
So this is going to be what GitHub calls a "project" site. Whatever we stick
in this branch (`gh-pages`; the name is important!), GitHub makes available
on the web. We have disabled Jekyll (see `.nojekyll` file) so GitHub Pages
does no further processing of the content beyond what we do. The docs URL
is

* [http://c0c0n3.github.io/ome-smuggler/docs/content/](http://c0c0n3.github.io/ome-smuggler/docs/content/)

Directories Layout
------------------
The docs sources (markdown, SVG, CSS, templates) go in `docs.src`; the app
to generate the docs is in `docs.build`; the `docs` directory is where the
generated output goes. The hierarchy of the output files mirrors that of
the input sources.

Bootstrap
---------
We use [Hakyll][hakyll] to generate a static web site out of `docs.src`.
To be able to run the Hakyll app you first have to install the Haskell
[stack][stack-docs] tool and then bootstrap the docs build system in
`docs.build`; this is a once-off procedure (for each new dev machine)
which you do like so:

    cd docs.build
    stack setup
    stack build

Additionally, every time you modify the Hakyll app in `docs.build` you
have to rebuild it with a `stack build`.

Workflow
--------
Author; build; publish. In detail:

1. Check out the `gh-pages` branch.
2. Remove the existing `docs` directory: `cd docs.build; stack exec clean`
3. Edit, add, remove content in `docs.src`.
4. Generate the docs: `cd docs.build; stack exec build`
5. Check out generated content in `docs`.
6. Go back to (3) until you're done authoring.
7. Make sure there are no broken links: `cd docs.build; stack exec check`
8. If there are any, fix them! i.e. go back to (3)...
9. Commit and push to GitHub.




[hakyll]: https://jaspervdj.be/hakyll/
    "Hakyll Home"

[stack-docs]: http://docs.haskellstack.org/en/stable/
    "stack Docs"
