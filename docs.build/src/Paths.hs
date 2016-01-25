module Paths where

import System.FilePath


-- NB for simplicity we assume the build is always run in `docs.build/`.
-- Typically this is true as the build instructions in the README say to
-- `cd` into `docs.build/`. (Also stack or cabal with default settings
-- wouldn't work if not run from `docs.build/`.)

baseDir  = "../"
srcDir   = baseDir </> "docs.src"
buildDir = "."
binDir   = baseDir </> "docs"
-- NB output goes directly into deployment dir so that all is left to do to
-- publish is commit and push to GitHub.
hakyllDir = buildDir </> ".hakyll"
