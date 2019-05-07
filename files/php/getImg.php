<?php

$usuario = $_POST["param1"];
$resp = "";

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
	$resultado = mysqli_query($con, "SELECT * FROM imagenes WHERE idusuario = '$usuario' ORDER BY id DESC");
	
	# Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
	$resp = 'Ha ocurrido algún error: ' . mysqli_error($con);
	echo $resp
	}else{
	#Acceder al resultado
	$fila = mysqli_fetch_row($resultado);
	
	$foto = $fila[1];

	echo base64_decode($foto);
	}
}
?>