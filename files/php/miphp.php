<?php
$DB_SERVER="255.255.255.255"; #la dirección del servidor
$DB_USER="usuario"; #el usuario para esa base de datos
$DB_PASS="password"; #la clave para ese usuario
$DB_DATABASE="nombreBD"; #la base de datos a la que hay que conectarse
# Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno($con)) {
echo 'Error de conexion: ' . mysqli_connect_error();
exit();
}else{
	
	
	
	
	
	
}
?>
