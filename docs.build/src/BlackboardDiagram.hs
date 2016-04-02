{-# LANGUAGE OverloadedStrings #-}
module BlackboardDiagram (makeFitBrowserWindow) where
    
import qualified Data.Map       as M
import qualified Data.Text.Lazy as T
import           Text.XML



-- 
-- Transforms an input SVG document to:
--   ⋅ set its width and height to "100%"
--   ⋅ style the document background with "background: #3c4747;"
--
-- The intention is to massage our blackboard diagrams so that they show nicely
-- in the browser when opened as standalone SVG. If the viewBox is set correctly
-- (Inkscape takes care of that), then the browser will centre the diagram and
-- make it fit the window. The background colour above is that of the BG layer
-- of the diagrams so that the browser doesn't display the diagram on a white
-- background.
--
-- NOTE.
-- As of 02 April 2016, I'm using an improved Inkscape blackboard template in
-- which the background colour is set using a style attribute on the svg root
-- element, so the setting of the background done by this module is redundant
-- for the new blackboard files.
-- But we need to keep it for backward compatibility as all the SVG diagrams
-- already in 'build.src' don't have the style attribute set on the svg root.
-- 
makeFitBrowserWindow :: String -> String
makeFitBrowserWindow = T.unpack . transformRoot makeFit . T.pack

transformRoot :: (Element -> Element) -> T.Text -> T.Text
transformRoot filter = renderText def . applyFilter . parseText_ def
    where
    applyFilter (Document prologue root epilogue) =
                 Document prologue (filter root) epilogue

makeFit :: Element -> Element
makeFit (Element _name attrs children) = Element _name attrs' children
    where
    attrs' = M.alter set100perc "height"         -- (1)
           $ M.alter set100perc "width"
           $ M.alter setBgColor "style" attrs
    set100perc = const $ Just "100%"
    setBgColor = const $ Just background         -- (2)
--
-- NOTES
-- (1) alter adds an entry (k, "100%") if the key k is missing or replaces
-- the old value if k is present.
-- (2) overrides blindly what was there, but we assume the file was produced
-- with Inkscape that doesn't add a style attribute to the the svg root element.
-- (Unless you tell it to…)

background = "background: #3c4747;"
-- not worth making colour a transformation input; keep it simple for now…
