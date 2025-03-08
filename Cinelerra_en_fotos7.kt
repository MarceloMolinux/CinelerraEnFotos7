//kotlin 2.1.0-release-394  febrero de 2025
import kotlin.io.path.readLines
import kotlin.io.path.Path
import kotlin.io.path.writeText
import kotlin.math.round


//---------------------------------------------------------------------


// constante global tiempo adicional
val tiempoAdicional:Int = 20  // → 5 fotogramas == 0.2 segundos

// constante global xmlEntrada: lista con el contenido del xml de entrada
val xmlEntrada:List<String> = leerArchivo() 

// constante global labels 
val labels = listaDeLabels()

// constante global marcas 
val marcas = calcularMarcas()

// constante global encabezado, es la que declara la versión y el directorio de trabajo
// val encabezado = buscarEDL()

//---------------------------------------------------------------------
//→ esta función se usa para generar la lista 'xmlEntrada'
// fun leerArchivo (archivo:String):List<String>{
fun leerArchivo ():List<String>{
    val texto:String = "VERSIÓN 7.1 TIENE MAS ZOOM, MAS MOVIMIENTO Y TRANSICIONES MAS LARGAS.\nHola, para usar este programa, tenés que preparar un proyecto en 'Cinelerra GG',  que cuente con una o varias pistas de audio, una pista de video con las diapositivas, y labels cuya ubicación corresponda al punto de transición entre diapositivas.\nLa pista de video debe llamarse 'Video 1' o 'Video 2' (con mayúscula), pero NUNCA 'Vídeo 1', es  decir, tenés que asegurarte que no lleve acento.\nPor favor, ingresá el nombre del proyecto que querés procesar (o renombralo 'entrada.xml')\nEl proyecto proyecto procesado se guardará como 'salida.xml' (Tené en cuenta que sobreescribe a un 'salida.xml' anterior, así que si querés conservarlo, tenes que renombrarlo.)\n**NOTA**  el programa 'Cinelerra en fotos' tiene que estar en la misma carpeta que tu proyecto.\n\n\nINGRESÁ EL NOMBRE DEL ARCHIVO A PROCESAR O DALE 'ENTER'"

    println(texto)
    var archivo = readLine() ?:""
    if (archivo == ""){
        archivo = "entrada.xml"
    }
    println("archivoingresado: $archivo")
    val ruta = Path(archivo)
    val xmlEntrada = ruta.readLines()
    return xmlEntrada
}

//---------------------------------------------------------------------

//→ esta función devuelve una lista con los labels expresados en FOTOGRAMAS
//→ 25 fotogramas corresponden a 1 segundo de video
fun listaDeLabels():List<Int>{
    val lineas = xmlEntrada
    val labels = mutableListOf<Int>() 
    val expresionRegular = """TIME=([0-9.e+-]+)""".toRegex()//para extraer labels
    //buscar los labels, PASARLOS a FOTOGRAMAS y guardarlos
    for (linea in lineas){ 
        if ("<LABEL TIME=" in linea) {
            val resultado = expresionRegular.find(linea)?.groupValues?.get(1)//VER MAS ABAJO(*)
                if (resultado != null) {
                    val label = round(resultado.toDouble() * 100 / 100 * 25).toInt()
                    labels.add(label)
                }
        }
    }
    return labels
}
/*
 * NOTA
 * (*)expresionRegular.find devuelve un objeto que tiene una propiedad llamada groupValues, y de 
     esta utilizamos el índice 1 ['.get(1)' ] que es el número q buscamos [y desechamos el indice 0]
*/ 

//---------------------------------------------------------------------
//devuelve la duración en fotogramas de cada diapositiva
fun duracionDiapositivas():List<Int>{
    val duracDiapositivas=mutableListOf<Int>()
    //calcular y guardar la duración de diapositovas
    for (x in 1 until labels.size){//until permite que no te salgas del rango (tb podría labels.size-1)
          duracDiapositivas.add((labels[x]-labels[x-1]) + 2*tiempoAdicional)
    }
    //falta la primera!! ahí va:
    duracDiapositivas.add(0,labels[0] + tiempoAdicional)
    
    return duracDiapositivas
}

//---------------------------------------------------------------------
//devuelve la duración en fotogramas de cada espaciador
fun duracionEspaciadores():List<Int>{
    val espaciadores=mutableListOf<Int>()
      
    //calcular y guardar la duración de espaciadores
    for (x in 1 until labels.size){//until permite que no te salgas del rango (tb podría labels.size-1)
          espaciadores.add((labels[x]-labels[x-1]) - 2*tiempoAdicional)
    }
    //falta la primera!! ahí va:
    espaciadores.add(0,labels[0] - tiempoAdicional)

    return espaciadores
}

//---------------------------------------------------------------------

//devuelve una lista anidada [[x1,x2],[x3,x4]]
fun calcularMarcas():List<List<Int>>{
    val marcas = mutableListOf<List<Int>>()

    for (x in labels){
        marcas.add(listOf(x-tiempoAdicional, x+tiempoAdicional))
    }
    return marcas
    
}
//---------------------------------------------------------------------
// devuelve lista <String> con las rutas de las diapositivas.
// ojo: tomarlas de "EDIT STARTSOURCE" porque "ASSERTS" incluye diapositivas borradas
fun archivosImagen():List<String>{
    val diapositivas = mutableListOf<String>()
    var texto:String=""
    var extension:String=""
    for (linea in xmlEntrada){
        if ("<EDIT STARTSOURCE=0" in linea){
            texto = linea.substringAfter("SRC=").substringBefore("></FILE></EDIT>")
            extension = texto.substring(texto.length-3, texto.length)
            if (extension == "JPG" || extension == "jpg" || extension== "PNG" || extension == "png" ){
                diapositivas.add(texto)       
            }  
        }
    }
    return diapositivas
                
}




//---------------------------------------------------------------------

// esta función construye el proyecto nuevo de Cinelerra
fun escribirTracks(){

    //---------------------------------
    // 1) construir las variables que serán incluidas en el texto de salida
    var video1:String = "" //para que acepte posterior concatenación las declaro como "VAR"
    var video2:String = ""//para que acepte posterior concatenación las declaro como "VAR"
    var fade:String   = "<AUTO POSITION=1 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
                        "<AUTO POSITION=1 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n" +
                        "<AUTO POSITION=${tiempoAdicional * 2} VALUE=100 VALUE1=0 " +
                        "CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"

    var cameraX1:String="<CAMERA_X>\n" +
                        "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" + 
                        "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"

    var cameraY1:String="<CAMERA_Y>\n" +
                        "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" + 
                        "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"

    var cameraZ1:String="<CAMERA_Z>\n" +
                        "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" + 
                        "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 " +
                        "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"

    var cameraX2:String= cameraX1
    var cameraY2:String= cameraY1
    var cameraZ2:String= cameraZ1


    val durDiapositivas =  duracionDiapositivas() // List<Int> de duraciones de las diapositivas
    val durEspaciadores = duracionEspaciadores() // List<Int> de duraciones de los espaciadores
    val diapositivas = archivosImagen() //rutas, texto a incluir en "ASSETS"!!!!!
    val deltaXY:Int = 20
    val deltaZ:Double =1.15
            
    // println("durDiapositivas tiene (elementos): ${durDiapositivas.size}")
    // println("diapositivas tiene (elementos): ${diapositivas.size}")
    // println("durDiapositivas\n ${durDiapositivas}")
    // println("diapositivas\n ${diapositivas}")

    //probamos que el número de labels coincida con el número de diapositivas
    val prueba:Int=(labels.size - diapositivas.size)
    if (prueba>0){
        println("por favor, agregá ${prueba} diapositivas")
    }else if (prueba<0){
        println("por favor, quitá ${-prueba} diapositivas")
    }else{
        println("todo ok, trabajando")
        println("Si el resultado no es bueno, fijate que la pista de video se llame 'Video 1'")
        println("o 'Video 2', pero nunca 'Vídeo' [con acento]")

    //en este punto (prueba=0), seguimos adelante
       for (i in diapositivas.indices){
            
            if (i % 2 == 0){ //indice cero o par
                
                video1=video1 + 
                "<EDIT STARTSOURCE=0 CHANNEL=0 LENGTH=${durDiapositivas[i]}" +
                "HARD_LEFT=0 HARD_RIGHT=0 COLOR=0 GROUP_ID=0><FILE SRC=${diapositivas[i]}></FILE></EDIT>\n" 

                video2=video2 +
                "<EDIT STARTSOURCE=0 CHANNEL=0 LENGTH=${durEspaciadores[i]}" + 
                "HARD_LEFT=0 HARD_RIGHT=0 COLOR=0 GROUP_ID=0></EDIT>\n"
            
                fade=fade +  
                "<AUTO POSITION=${marcas[i][0]} VALUE=100 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n" +
                "<AUTO POSITION=${marcas[i][1]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"

                cameraX1=cameraX1 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaXY} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraX2=cameraX2 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraY1=cameraY1 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaXY} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraY2=cameraY2 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraZ1=cameraZ1 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaZ} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraZ2=cameraZ2 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

            }else{
                    
                video1=video1 + 
                "<EDIT STARTSOURCE=0 CHANNEL=0 LENGTH=${durEspaciadores[i]}" + 
                "HARD_LEFT=0 HARD_RIGHT=0 COLOR=0 GROUP_ID=0></EDIT>\n"

                video2=video2 +
                "<EDIT STARTSOURCE=0 CHANNEL=0 LENGTH=${durDiapositivas[i]}" +
                "HARD_LEFT=0 HARD_RIGHT=0 COLOR=0 GROUP_ID=0><FILE SRC=${diapositivas[i]}></FILE></EDIT>\n" 
                
                fade=fade +  
                "<AUTO POSITION=${marcas[i][0]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n" +
                "<AUTO POSITION=${marcas[i][1]} VALUE=100 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=0></AUTO>\n"
                
                cameraX1=cameraX1 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraX2=cameraX2 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaXY} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraY1=cameraY1 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraY2=cameraY2 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaXY} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraZ1=cameraZ1 +
                "<AUTO POSITION= ${marcas[i][0]} VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"

                cameraZ2=cameraZ2 +
                "<AUTO POSITION= ${marcas[i][1]} VALUE=${deltaZ} VALUE1=0 CONTROL_IN_VALUE=0 " +
                "CONTROL_OUT_VALUE=0 TANGENT_MODE=1></AUTO>\n"
            } 
        }


        //---------------------------------
        // 2) constituir el texto de salida  //SEGUIR ACÁ!!!!!!
        val posVideo1 = xmlEntrada.indexOfFirst { "<TITLE>Video" in it }
        val posAudio1 = xmlEntrada.indexOfFirst { "<TITLE>Audio" in it }
        val parte1 = xmlEntrada.slice(0..posVideo1).joinToString("\n")//obtenemos slice que pasamos a texto
        val parte2 = xmlEntrada.slice(posAudio1..xmlEntrada.size-1).joinToString("\n")
        val salida:String =
            parte1 +
            "<TITLE>Video 1</TITLE> \n<EDITS>" +
            video1 +
            "</EDITS>\n<MUTEAUTOS>\n<AUTO POSITION=0 VALUE=0></AUTO>\n</MUTEAUTOS>\n" +
            cameraX1 + "</CAMERA_X>\n" +
            cameraY1 + "</CAMERA_Y>\n" +
            cameraZ1 + "</CAMERA_Z>\n" +
            "<PROJECTOR_X>\n"  +
            "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_X>\n" + "<PROJECTOR_Y>\n" +
            "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_Y>\n" + "<PROJECTOR_Z>\n" +
            "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_Z>\n" + "<FADEAUTOS>" +
            fade + "</FADEAUTOS>\n" +
            "<MODEAUTOS>\n" + "<AUTO POSITION=0 VALUE=0></AUTO>\n" + "</MODEAUTOS>\n" + "<MASKAUTOS>\n" +
            "<AUTO APPLY_BEFORE_PLUGINS=0 DISABLE_OPENGL_MASKING=0 POSITION=0>\n" + "</AUTO>\n" +
            "</MASKAUTOS>\n" + "<SPEEDAUTOS>\n" +
            "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</SPEEDAUTOS>\n" + "</TRACK>\n" +
            "<TRACK RECORD=0 NUDGE=0 MIXER_ID=-1 PLAY=1 GANG=1 MASTER=1 DRAW=1 EXPAND=0 DATA_H=64 TRACK_W=1920 " +
            "TRACK_H=1080 MASKS=255 TYPE=VIDEO>\n" + "<TITLE>Video 2</TITLE>\n" + "<EDITS>\n" +
            video2 +
            "</EDITS>\n<MUTEAUTOS>\n<AUTO POSITION=0 VALUE=0></AUTO>\n</MUTEAUTOS>\n" +
            cameraX2 + "</CAMERA_X>\n" +
            cameraY2 + "</CAMERA_Y>\n" +
            cameraZ2 + "</CAMERA_Z>\n" +
            "<PROJECTOR_X>\n"  +
            "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_X>\n" + "<PROJECTOR_Y>\n" +
            "<AUTO POSITION=0 VALUE=0 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_Y>\n" + "<PROJECTOR_Z>\n" +
            "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</PROJECTOR_Z>\n" + "<FADEAUTOS>" +
            "</FADEAUTOS>\n" + //esta pista no lleva fade
            "<MODEAUTOS>\n" + "<AUTO POSITION=0 VALUE=0></AUTO>\n" + "</MODEAUTOS>\n" + "<MASKAUTOS>\n" +
            "<AUTO APPLY_BEFORE_PLUGINS=0 DISABLE_OPENGL_MASKING=0 POSITION=0>\n" + "</AUTO>\n" +
            "</MASKAUTOS>\n" + "<SPEEDAUTOS>\n" +
            "<AUTO POSITION=0 VALUE=1 VALUE1=0 CONTROL_IN_VALUE=0 CONTROL_OUT_VALUE=0 TANGENT_MODE=3></AUTO>\n" +
            "</SPEEDAUTOS>\n" + "</TRACK>\n" +
            "<TRACK RECORD=0 NUDGE=0 MIXER_ID=-1 PLAY=1 GANG=1 MASTER=0 DRAW=1 EXPAND=0 DATA_H=64 TRACK_W=1920 " +
            "TRACK_H=1080 MASKS=255 TYPE=AUDIO>\n"+
            parte2
                   
        //---------------------------------
        // 3) escribir en el archivo de salida
        // EJEMPLO DE COMO GUARDAR TEXTO EN UN ARCHIVO!!!!!!!!!!
        val rutaSalida = Path("salida.xml")
        println("Todo listo :)  Cargá en Cinelerra el archivo $rutaSalida")
        rutaSalida.writeText(salida)

        //println("parte1 $parte1[0]")
    }    
 }   

//--------------------------------------------------------------------------
// función principal
fun main() {
    escribirTracks()
}

//--------------------------------------------------------------------------
// llamada a la función principal
//main()
