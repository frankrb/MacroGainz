<?php

$usuario = $_POST["param1"];
//$usuario = "T";
$token ="";

$token = getToken($usuario);
//echo "\n\n Final token value: ". $token;

$tokens = [$token];

//key-> clave del servidor
$cabecera= array(
'Authorization: key=AAAAF8v9Q6c:APA91bHp_T4kQ9U193uo8ceF5ah_zuKVIZckSXXllx0YUMrPH4DZJGRDiXsixHeti87zUEJjVUICTPfIwpAie1bjLKzFfpaWPPQi6nX2_TEFeuVi1KuEOQr9HImNJNecDg55zt7tAHra',
'Content-Type: application/json'
);
$msg = array (
'registration_ids' => $tokens,
'notification' => array (
"body" => "Te has suscrito a las notificaciones de ejercicios!",
"title" => "Notificaciones de ejercicios",
"icon" => "logo")
);

$msgJSON= json_encode ( $msg);

//echo "\n\n --- ". msgJSON;

$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );
#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
#ejecutar la llamada
$resultado= curl_exec( $ch );
#cerrar el handler de curl
curl_close( $ch );

#depuramos herrores
if (curl_errno($ch)) {
//echo "\n\n Error: ". curl_error($ch);
}else{
//echo "\n\n Resultado: ". $resultado;
}

$resp = "correcto";
$arrayresultados = array('respuesta' => $resp);
	
#Devolver el resultado en formato JSON
echo json_encode($arrayresultados);


##obtener el últomo token del usuario desde mysql server
function getToken($user){
$token = "";

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xframos001"; #el usuario para esa base de datos
$DB_PASS="ESA2IrN7"; #la clave para ese usuario
$DB_DATABASE="Xframos001_macrogainz"; #la base de datos a la que hay que conectarse

# Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos conexión
if (mysqli_connect_errno($con)) {
echo 'Error de conexion: ' . mysqli_connect_error();
exit();
}else{
	#lo que queramos hacer 

	# Ejecutar la sentencia SQL
	$resultado = mysqli_query($con, "SELECT * FROM Tokens WHERE idusuario = '$user' ORDER BY id DESC");
	
	# Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}else{
	#accedemos a los valores del resultado
	
	#Acceder al resultado
	$fila = mysqli_fetch_row($resultado);
	$resp = "correcto";
	
	# Generar el array con los resultados con la forma Atributo - Valor
	$arrayresultados = array('id' => $fila[0],'token'=>$fila[1],'iduser'=>$fila[2]);
	
	#Devolver el resultado en formato JSON
	//echo json_encode($arrayresultados);
	//echo "\n\n";
	$token = $fila[1];
	//echo $token;
	}
}
##finalizamos la obtención del token
    return $token;
}
##


?>