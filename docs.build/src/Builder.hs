{-# LANGUAGE OverloadedStrings #-}
module Builder (run) where

import Hakyll
import System.FilePath

import Compilers
import Paths


run :: IO ()
run = hakyllWith config $ do

    match "templates/**" $ compile $ templateCompiler

    match "css/**" $ do
        route idRoute
        compile compressCssCompiler

    match ("images/**" .||. "pdfs/**") $ do
        route idRoute
        compile copyFileCompiler

    match "content/**.svg" $ do
        route idRoute
        compile svgToFullScreenSvg
                
    match "content/**.md" $ do
        route   $ setExtension ".html"
        compile $ markdownToHtml5
                >>= loadAndApplyTemplate "templates/page.html" defaultContext
                >>= relativizeUrls

    
config :: Configuration
config = defaultConfiguration
       { providerDirectory    = srcDir 
       , destinationDirectory = binDir
       , storeDirectory       = hakyllDir
       , tmpDirectory         = hakyllDir </> "tmp"
       }

