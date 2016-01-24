{-# LANGUAGE OverloadedStrings #-}
module Builder (run) where

import Hakyll
import System.FilePath
import Text.Pandoc.Options
       
import Paths


run :: IO ()
run = hakyllWith config $ do

    match "templates/**" $ compile $ templateCompiler

    match "css/**" $ do
        route idRoute
        compile compressCssCompiler

    match "content/**.svg" $ do
        route idRoute
        compile copyFileCompiler

    match "content/**.md" $ do
        route   $ setExtension ".html"
        compile $ toHtml
                >>= loadAndApplyTemplate "templates/page.html" defaultContext
                >>= relativizeUrls


toHtml = pandocCompilerWith readerOptions writerOptions
    where
    readerOptions = def
    writerOptions = def { writerHtml5 = True }
    
config :: Configuration
config = defaultConfiguration
       { providerDirectory    = srcDir 
       , destinationDirectory = binDir
       , storeDirectory       = hakyllDir
       , tmpDirectory         = hakyllDir </> "tmp"
       }

