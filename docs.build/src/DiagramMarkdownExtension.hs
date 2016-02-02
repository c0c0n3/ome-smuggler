{-# LANGUAGE QuasiQuotes, FlexibleContexts #-}
{-
Transform any "diagram block" in an input Pandoc into an HTML5 figure block.
A "diagram block" is an SVG image with caption and is encoded in markdown as
as div, e.g.

<div class="diagram" id="fig1"
     src="my/pic.svg">
  Some caption here <em>with html</em> and
  some **markdown**.</div>

We expect it to have class="diagram", if not the div is ignored. Also id and
src must be provided as valid values as we run no sanity checks. 
The div content is the caption and can be any Pandoc markdown.

See example at the bottom of this file.
-}
module DiagramMarkdownExtension (renderDiagramsAsHtml5) where

import Data.String.Here
import Text.Pandoc
import Text.Pandoc.Builder
import Text.Pandoc.Walk       


renderDiagramsAsHtml5 :: Walkable Block b => b -> b
renderDiagramsAsHtml5 = walk insertFigure

type Html5  = String
type FigId  = String
type FigUrl = String
-- types could be better and stronger, but let's keep it simple for now...

insertFigure :: Block -> Block
insertFigure (Div (id, ["diagram"], [("src", url)]) caption) =
             buildFigure id url caption
insertFigure x = x

buildFigure :: FigId -> FigUrl -> [Block] -> Block
buildFigure id url caption = RawBlock (Format "html") figure
    where
    figure      = buildFromTemplate id url htmlCaption
    htmlCaption = writeHtmlString def $ doc $ fromList caption

buildFromTemplate :: FigId -> FigUrl -> Html5 -> Html5 
buildFromTemplate id url caption = [i|
<figure id="${id}">
  <div><a href="${url}">view full screen</a></div>
  <object type="image/svg+xml" data="${url}">
  your browser doesn't support embedded SVG; 
  click on the link above to see the image.
  </object>
  <figcaption>
    ${caption}
  </figcaption>
</figure>
|]
{- NOTES
 (1) We should do better than this, e.g. use Lucid or Yesod. Quick & dirty is
     good enough for now though...
 (2) Embed fallback. We could insert <img src="..."> but if the diagram has
     links in it, then the user won't be able to click on them. So it's best
     to actually redirect them to the standalone SVG.
-}

{- EXAMPLE
    
input = [here|
---
title: test
---

Headbutt
========
blah blah blah

<div class="diagram" id="fig1"
     src="my/pic.svg">
  Some caption here <em>with html</em> and
  some **markdown**.</div>

some other stuff...
|]
    
md   = readMarkdown def input 
md'  = walk insertFigure md
html = writeHtmlString def md'

-}
