module Compilers
       ( markdownToHtml5
       , svgToFullScreenSvg
       ) where

import Hakyll
import Text.Pandoc.Options

import BlackboardDiagram
import DiagramMarkdownExtension



markdownToHtml5 :: Compiler (Item String)
markdownToHtml5 = pandocCompilerWithTransform r w renderDiagramsAsHtml5
    where
    r = def
    w = def { writerHtml5 = True
            , writerHighlight = True
            }
--    r = defaultHakyllReaderOptions
--    w = defaultHakyllWriterOptions { writerHtml5 = True } 

svgToFullScreenSvg :: Compiler (Item String)
svgToFullScreenSvg =   getResourceString
                   >>= return . fmap makeFitBrowserWindow
