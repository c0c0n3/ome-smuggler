module Compilers
       ( markdownToHtml5
       , svgToFullScreenSvg
       ) where

import Hakyll
import Text.Pandoc.Options

import BlackboardDiagram
import DiagramMarkdownExtension



markdownToHtml5 :: Compiler (Item String)
markdownToHtml5 = pandocCompilerWithTransform rOpt wOpt renderDiagramsAsHtml5
    where
    rOpt = def { readerSmart = True }
    wOpt = def { writerHtml5 = True
               , writerHighlight = True
               }

svgToFullScreenSvg :: Compiler (Item String)
svgToFullScreenSvg =   getResourceString
                   >>= return . fmap makeFitBrowserWindow
