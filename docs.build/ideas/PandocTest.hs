{-# LANGUAGE QuasiQuotes #-}
module Test where

import Data.String.Here
import Text.Pandoc
    
md = [hereLit|
---
m1: v1
m2: aaaaa
    bbbbbb 

    cccc
---
h1
==
some par, with some <em>HTML TAG</em>!
still same par

new par

h2
==
text with interspersed

---
m3: v3
m4: v4
---

metadata
<div class=x>
  <mytag k=v>blah</mytag>
</div>
<figure>
  <img src='1'/>
  <figcaption>
cap
  </figcaption>
</figure>
|]

parsed = readMarkdown def md
