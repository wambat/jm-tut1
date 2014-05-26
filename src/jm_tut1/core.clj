(ns jm-tut1.core
  (:import [com.jme3 app.SimpleApplication
            material.Material
            material.RenderState
            material.RenderState$BlendMode
            light.DirectionalLight
            scene.Geometry
            system.AppSettings
            system.JmeSystem
            renderer.queue.RenderQueue$Bucket
            scene.shape.Box
            scene.Node
            math.Vector3f
            math.ColorRGBA]
   ))
(def desktop-cfg (.getResource (.getContextClassLoader (Thread/currentThread))
                   "com/jme3/asset/Desktop.cfg"))

(def assetManager (JmeSystem/newAssetManager desktop-cfg))
 
(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "jm-tut1")))

(def speed 5)

(defn init [app]
  (let [b1 (Box. (Vector3f. 1 -1 1)  1 1 1)
        b1geom (Geometry. "Box" b1)
        b1mat (Material. assetManager
                         "Common/MatDefs/Misc/Unshaded.j3md")
        b2 (Box. (Vector3f. 1 -1 3)  1 1 1)
        b2geom (Geometry. "Box" b2)
        b2mat (Material. assetManager
                         "Common/MatDefs/Misc/Unshaded.j3md")
        l1 (DirectionalLight.)
        pivot (Node. "pivot")
        ]
    (org.lwjgl.input.Mouse/setGrabbed false)
    (.setColor b1mat "Color" (ColorRGBA. 0.1 0 1 0.5))
    (.setColor b2mat "Color" (ColorRGBA. 0.5 0 0 0.5))
    (.setQueueBucket b1geom RenderQueue$Bucket/Transparent)
    (.setQueueBucket b2geom RenderQueue$Bucket/Transparent)
    (.setBlendMode (.getAdditionalRenderState  b2mat) RenderState$BlendMode/Alpha ) 
    (.setBlendMode (.getAdditionalRenderState  b1mat) RenderState$BlendMode/Alpha ) 
    (.setMaterial b1geom b1mat)
    (.setMaterial b2geom b2mat)
    (.setColor l1 (ColorRGBA/Blue))
    (.setDirection l1 (.normalizeLocal (Vector3f. 1 0 -2)))
    (doto pivot 
      (.attachChild b1geom)
      (.attachChild b2geom)
      )
    (doto (.getRootNode app) 
      (.addLight l1)
      (.attachChild pivot))
    (.rotate pivot 0.4 0.4 0)))

(declare update)
(def app (proxy [SimpleApplication] []
             (simpleInitApp []
               (init this))
             (simpleUpdate [tpf]
               (update this tpf))
))

(defn update [app tpf]
  (let [subj (.getChild (.getRootNode app) "pivot") ] 
    (.rotate subj 0 (* 2 tpf) 0))
  )

(defn stop []
  (doto app
    (.stop))
  )
(defn -main [& args]
  (doto app
    (.setShowSettings false)
    (.setPauseOnLostFocus false)
    (.setSettings *app-settings*)
    (.start)))
