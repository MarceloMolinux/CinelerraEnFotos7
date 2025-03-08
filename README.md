# Cinelerra_en_fotos7  (the English version can be found below.)

Este programa, (**'Cinelerra_en_fotos7.kt'**), escrito en Kotlin, tiene el propósito de procesar un archivo de proyecto de **'Cinelerra GG'** (diapositivas y música), para obtener un nuevo proyecto, en el cual las transiciones entre diapositivas estén sincronizadas con la música. Luego, este nuevo proyecto debe ser cargado en Cinelerra GG y renderizado.

Nota: Inspirado en un script publicado por Michal Fapso en http://michalfapso.blogspot.com/2010/12/how-to-create-photo-slideshow.html

## Para compilar:
```bash
kotlinc Cinelerra_en_fotos7.kt -include-runtime -d Cinelerra_en_fotos7.jar
```

## Para ejecutar:
```bash
java -jar Cinelerra_en_fotos7.jar
```

## Instrucciones para usar el programa:

Para usar este programa, tenés que preparar un proyecto en 'Cinelerra GG' que cuente con:

1. Una o varias pistas de audio.
2. Una pista de video con las diapositivas.
3. Labels, cuya ubicación corresponda al punto de transición entre las diapositivas.

La pista de video debe llamarse **'Video 1'** o **'Video 2'** (con mayúscula), pero NUNCA **'Vídeo 1'** (ni 'Vídeo 2', ni 'video1', ni 'pepe'... ¡asegurate de que no lleve acento! Los programas traducidos al español suelen usar la palabra 'Vídeo' y eso genera problemas).

### Procedimiento:

#### Primera parte: usando CINELERRA GG

1. En el menú **'Settings'** → **'Edit labels'** debe estar desactivado (ver que el ícono de 'lock labels' en la barra de botones esté iluminado). Esto es importante porque, al editar el video, si no bloqueas los labels, estos se moverán (y lo que querés es que estén fijos en la línea de tiempo).
   
2. Cargar en Cinelerra GG la pista de música, reproducirla e ir tecleando **'l'** (de label) cuando creas que deben ocurrir las transiciones entre diapositivas.

3. Cargar las diapositivas en una pista de video (debe haber la misma cantidad que de labels) y guardar el proyecto (por ejemplo, 'entrada.xml').

#### Segunda parte: usando Cinelerra_en_fotos7

4. En el mismo directorio del proyecto, copiar el archivo **'Cinelerra_en_fotos7.jar'**. Abrir una terminal y teclear:
   
   ```bash
   java -jar Cinelerra_en_fotos7.jar
   ```

5. Luego presionar enter, y nuevamente enter (o ingresar el nombre del archivo XML si no es 'entrada.xml').
   Se debería haber formado un archivo **'salida.xml'**

#### Tercera parte: (de nuevo) usando CINELERRA GG

6. Verificar que se haya formado **'salida.xml'**. Abrirlo con Cinelerra, revisarlo, retocar lo necesario y renderizarlo.

7. ¡Bualá!

---
Autor: Marcelo Molina - Febrero 2025  
kotlin 2.1.0-release-394

---

# Cinelerra_en_fotos7 (English version)

This program, (**'Cinelerra_en_fotos7.jar'**), written in Kotlin, is designed to process a **'Cinelerra GG'** project file (slides and music) to create a new project in which slide transitions are synchronized with the music. Then, this new project must be loaded into Cinelerra GG and rendered.

Note: Inspired by a script published by Michal Fapso at http://michalfapso.blogspot.com/2010/12/how-to-create-photo-slideshow.html

## Compilation:
```bash
kotlinc Cinelerra_en_fotos7.kt -include-runtime -d Cinelerra_en_fotos7.jar
```

## Execution:
```bash
java -jar Cinelerra_en_fotos7.jar
```

## Instructions for using the program:

To use this program, you need to prepare a project in 'Cinelerra GG' that includes:

1. One or more audio tracks.
2. A video track with the slides.
3. Labels, placed at the transition points between slides.

The video track must be named **'Video 1'** or **'Video 2'** (capitalized), but NEVER **'Vídeo 1'** (nor 'Vídeo 2', 'video1', or 'pepe'... make sure it has no accent! Spanish-translated programs often use 'Vídeo', which causes issues).

### Procedure:

#### First part: using CINELERRA GG

1. In the **'Settings'** → **'Edit labels'** menu, make sure it is disabled (check that the 'lock labels' icon in the button bar is highlighted). This is important because if you do not lock the labels, they will move when editing the video (and you want them fixed on the timeline).
   
2. Load the music track into Cinelerra GG, play it, and press **'l'** (for label) when you think transitions between slides should occur.

3. Load the slides into a video track (there must be the same number of slides as labels) and save the project (e.g., 'entrada.xml').

#### Second part: using Cinelerra_en_fotos7

4. Copy **'Cinelerra_en_fotos7.jar'** into the same project directory. Open a terminal and type:
   
   ```bash
   java -jar Cinelerra_en_fotos7.jar
   ```

5. Press enter, and then enter again (or enter the XML file name if it is not 'entrada.xml').
   A **'salida.xml'** file should have been generated.

#### Third part: (again) using CINELERRA GG

6. Verify that **'salida.xml'** has been created. Open it in Cinelerra, review it, make any necessary adjustments, and render it.

7. Voilà!

---
Author: Marcelo Molina - February 2025  
kotlin 2.1.0-release-394

