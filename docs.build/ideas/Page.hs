{-# LANGUAGE QuasiQuotes #-}
-- poor man's (safer) doc generation
module Page where

import Data.String.Here
import Text.Pandoc

-- page.md metadata
title = "my title"

-- type-safe external links
data Hrefs = Link1 | Link2
instance Show Hrefs where
    show Link1 = "http://1/"
    show Link2 = "http://2/"             

-- type-safe internal links
data Refs = Ref1
instance Show Refs where
    show Ref1 = "1/other.html"

-- figures
data Figs = Fig1
instance Show Figs where
    show Fig1 = "my.svg"

-- NOTE: can easily build a graph out of the above; it can then be used for
-- site maps, navigation, etc.
                
-- HTML gen; use combinators (e.g. Lucid) instead of interpolation?
-- if going for templates, then Yesod is a better option…
fig x = [i|
<figure>
  <img src="${show x}"/>  
</figure>
|]

href x y = [i| <a href="${show x}">${id y}</a> |]
ref = href 
-- possibly a better option is to use markdown refs, e.g.
--  
--  this is [a link desc]${link SomeId}
--
-- would produce: this is [a link desc][SomeId]
-- and at the bottom of the file:
--   [SomeId]: http://example.com/  "Optional Title Here"

    
md = [i|---
title: ${title}
---

|]
   ++ [template|page.md|]

parsed = readMarkdown def md

-- NOTE Haskell code for a page can be processed using Hakyll, in the same
-- way as explained in the Clay integration tut:
-- * https://jaspervdj.be/hakyll/tutorials/using-clay-with-hakyll.html
