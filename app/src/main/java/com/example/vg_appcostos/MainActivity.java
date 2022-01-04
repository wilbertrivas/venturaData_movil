package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_ZonaTrabajo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Usuario;
import com.example.vg_appcostos.Sistemas.EncriptarPassword;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText usuario;
    EditText password;
    //EditText num1SoapS, num2SoapS;
    Button bntIngresarT,btnIngresarMail;
    //TextView resultadoSoap;
    Spinner spinnerTipoConexion,spinnerZonaTrabajo;
    AlertDialog.Builder builder;
    ArrayList<ZonaTrabajo> listadoZonaTrabajo =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usuario=(EditText)findViewById(R.id.editUsuario);
        password=(EditText)findViewById(R.id.editContrasena);
        bntIngresarT=(Button)findViewById(R.id.btnIngresar);
        //btnIngresarMail=(Button)findViewById(R.id.btnIngresarMail);
        //resultadoSoap= (TextView) findViewById(R.id.resultSoap);
        //num1SoapS= (EditText) findViewById(R.id.num1Soap);
        //num2SoapS= (EditText) findViewById(R.id.num2Soap);
        EncriptarPassword encrip = new EncriptarPassword();

        //Cargamos el tipo de conexión el la app
        spinnerTipoConexion= (Spinner)findViewById(R.id.spinnerTipoConexion);
        spinnerZonaTrabajo= (Spinner)findViewById(R.id.spinnerZonaTrabajo);
        ArrayList<String> arrayListadoT =new ArrayList<>();
        arrayListadoT.add("PUBLICO");
        arrayListadoT.add("PRIVADO");
        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
        spinnerTipoConexion.setAdapter(adaptadorListado);
        //usuario.setText("1111786243");
        //password.setText("Wr1v4s1992$");
        //usuario.setText("6246120");
        //password.setText("Ernest0.2021");


        builder = new AlertDialog.Builder(this);

        //Cargamos las Zonas de Trabajo
        try {

            listadoZonaTrabajo = new ControlDB_ZonaTrabajo("publico").buscarZonasTrabajosActivas();
            if (listadoZonaTrabajo != null) {
                ArrayList<String> arrayListadoz =new ArrayList<>();
                for (ZonaTrabajo zonaTrabajo : listadoZonaTrabajo) {
                    arrayListadoz.add(zonaTrabajo.getDescripcion());
                }
                ArrayAdapter<String> adaptadorListadoZ = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoz);
                spinnerZonaTrabajo.setAdapter(adaptadorListadoZ);
            }
        }catch (Exception e){
            e.printStackTrace();
            listadoZonaTrabajo = new ControlDB_ZonaTrabajo("privado").buscarZonasTrabajosActivas();
            if (listadoZonaTrabajo != null) {
                ArrayList<String> arrayListadoz =new ArrayList<>();
                for (ZonaTrabajo zonaTrabajo : listadoZonaTrabajo) {
                    arrayListadoz.add(zonaTrabajo.getDescripcion());
                }
                ArrayAdapter<String> adaptadorListadoZ = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoz);
                spinnerZonaTrabajo.setAdapter(adaptadorListadoZ);
            }
        }



        /*btnIngresarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MailJob().execute(
                        new MailJob.Mail( "wrivas@oppgraneles.com", "Por Fin", "Esta es una prueba")

                );
                System.out.println("===========================================================================>Se fue");
                /*try {
                    Usuario usu = new Usuario();
                    usu.setNombres("Wilbert");
                    usu.setApellidos("Rivas Granados");
                    usu.setCorreo("wrivas@oppgraneles.com");
                    new Mail("publico").enviarMailPorRegistroRecobro(usu,"Carbón");
                    String cuerpo="No entro";
                    System.out.println("34===============================================================================================>"+cuerpo);
                } catch (SQLException e) {
                    String cuerpo="No entro";
                    System.out.println("78===============================================================================================>"+cuerpo);

                    e.printStackTrace();
                }*/
               // Mail m = new Mail()
           /* }
        });*/

        bntIngresarT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usuario.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Error!.. El usuario no puede estar vacio", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Error!.. La contraseña no puede estar vacia", Toast.LENGTH_SHORT).show();
                    } else {
                        String typeConnection="";
                        if(spinnerTipoConexion.getSelectedItem().toString().equals("PUBLICO")){
                            typeConnection=  "publico";
                        }else{//Conexión de tipo privado
                            typeConnection=  "privado";
                        }
                        //Buscamos en nuestra base de datos si el usuario y password con correcto
                        ControlDB_Usuario controlDB_Usuario = new ControlDB_Usuario(typeConnection);
                        Usuario u = new Usuario();
                        u.setCodigo(usuario.getText().toString());
                        u.setClave(password.getText().toString());
                        Usuario user = controlDB_Usuario.validarUsuario(u);
                        if (user != null) {
                            if (user.getEstado().toString().equalsIgnoreCase("ACTIVO")) {
                                Intent interfaz = new Intent(MainActivity.this, Menu.class);
                                //Intent interfaz = new Intent(MainActivity.this, Main2Activity.class);
                                interfaz.putExtra("usuarioB",  user);
                                interfaz.putExtra("TipoConexion",  typeConnection);
                                interfaz.putExtra("zonaTrabajoSeleccionada",  listadoZonaTrabajo.get(spinnerZonaTrabajo.getSelectedItemPosition()));

                                //interfaz.putExtra("codigo",user.getCodigo());
                                //interfaz.putExtra("nombre",user.getNombres()+" "+user.getApellidos());
                                //interfaz.putExtra("contrasena",password.getText().toString());
                                startActivity(interfaz);
                            } else {
                                //Toast.makeText(getApplicationContext(), "Error!.. El usuario no se encuentra activo en el sistema", Toast.LENGTH_SHORT).show();
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Advertencia!!");
                                builder.setMessage("El usuario no se encuentra activo en el sistema");
                                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                        } else {
                            //Toast.makeText(getApplicationContext(), "Error!.. Usuario o Contraseña Incorrecta verifique datos", Toast.LENGTH_SHORT).show();
                            builder = new AlertDialog.Builder(builder.getContext());
                            builder.setTitle("Advertencia!!");
                            builder.setMessage("Usuario o Contraseña Incorrecto, verifique datos");
                            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent interfaz = new Intent(MainActivity.this, MainActivity.class);
            startActivity(interfaz);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*public void enviarOnclick(View v){
        Thread nt= new Thread(){
            String res;
            @Override
            public void run(){
                String namespace="http://costos_vg_ws.org/";
                String URL="http://172.25.80.81/WS_Costos_VG/Costos_VG_WS.asmx";
                String method_name="suma";
                String SOAP_ACTION="http://costos_vg_ws.org/suma";
                SoapObject request= new SoapObject(namespace,method_name);
                request.addProperty("num1", Integer.parseInt(num1SoapS.getText().toString()));
                request.addProperty("num2", Integer.parseInt(num2SoapS.getText().toString()));
                SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE transporte = new HttpTransportSE(URL);
                try {
                    transporte.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado_xml= (SoapPrimitive) envelope.getResponse();
                    res= resultado_xml.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultadoSoap.setText("="+res);
                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        nt.start();
    }*/
   /*public void validarUsuarioOnclick(View v){
        Thread nt= new Thread(){
            String res;
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run(){
                String namespace="http://costos_vg_ws.org/";
                String URL="http://172.25.80.81/WS_Costos_VG/Costos_VG_WS.asmx";
                String method_name="validarUsuario";
                String SOAP_ACTION="http://costos_vg_ws.org/validarUsuario";
                SoapObject request= new SoapObject(namespace,method_name);
                //request.addProperty("num1", Integer.parseInt(num1SoapS.getText().toString()));
                //request.addProperty("num2", Integer.parseInt(num2SoapS.getText().toString()));
                SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE transporte = new HttpTransportSE(URL);
                try {
                    transporte.call(SOAP_ACTION,envelope);
                    SoapObject resSoap =(SoapObject)envelope.getResponse();
                    Usuario us = new Usuario();
                    ArrayList<Usuario> listadoUsuario;
                    listadoUsuario = new ArrayList<Usuario>();

                    for (int i = 1; i < resSoap.getPropertyCount()-1; i++)
                    {
                        SoapObject ic = (SoapObject)resSoap.getProperty(i);

                        Usuario cli = new Usuario();
                        cli.setCodigo(ic.getProperty(0).toString());
                        cli.setNombres(ic.getProperty(1).toString());


                        listadoUsuario.add(cli);
                    }
                    System.out.println(listadoUsuario.get(0).getCodigo());
                    //Dataset data;
                    //data = (Dataset) envelope.getResponse();
                    /*
                    SoapObject response = (SoapObject) envelope.getResponse();
                    SoapObject pii = (SoapObject)response.getProperty(1);
                    System.out.println( pii.getNamespace());
                    //(//String[] dvector=new String[pii.getPropertyCount()];
                    pii.getName();
                    //dvector= pii.getProperty(1);

                    System.out.println(pii.getProperty(1).toString());
                   // SoapObject soapBook = (SoapObject) response.getProperty(1);
                    // System.out.println(soapBook.getAttribute("us_clave").toString());
                    /*for(int i=0; i<response.getPropertyCount()-1;i++){
                        SoapObject pii = (SoapObject)response.getProperty(1);
                        System.out.println(pii.getProperty(1).toString());

                    }*/
                    /*System.out.println(response.getPropertyCount());
                    System.out.println(response.getProperty(1).toString());
                    SoapObject pii = (SoapObject) response.getProperty(1);
                    System.out.println(pii.getProperty(0).toString());*/
                    //String c = response.getProperty(1).toString();
                    //System.out.println(c);
                    //SoapObject pii = (SoapObject)response.getProperty(1);
                    //System.out.println(pii.getProperty(1).toString());
                    /*for(int i=0;i<response.getPropertyCount(); i++){
                        SoapObject soapBook = (SoapObject) response.getProperty(i);


                        System.out.println(soapBook.getAttribute("us_clave").toString());
                      //  resultadoSoap.setText(""+soapBook.getProperty(i).toString());
                        //Toast.makeText(MainActivity.this, soapBook.getProperty(i).toString(), Toast.LENGTH_SHORT).show();
                    }*/

                    /*
                    String responseCode = response.getPrimitivePropertyAsString("ResponseCode");
                    String responseMessage = response.getPrimitivePropertyAsString("Message");

                    SoapObject soapBooksCatalog = (SoapObject) response.getProperty("Catalog");
                    int booksCount = soapBooksCatalog.getPropertyCount();
                    //  booksCount = 3 (because we have 3 <Book> objects inside <Catalog>)

                    List<Book> booksCatalog = new ArrayList<>();

                    for (int currentBook = 0; currentBook < booksCount; currentBook++) {

                        SoapObject soapBook = (SoapObject) soapBooksCatalog.getProperty(currentBook);
                        // Each iteration soapBook is filled with the info of a different <Book> object

                        Book book = new Book();
                        book.setAutor(soapBook.getPrimitivePropertyAsString("Author"));
                        book.setTitulo(soapBook.getPrimitivePropertyAsString("Title"));

                        booksCatalog.add(book);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultadoSoap.setText("="+res);
                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        nt.start();
    }*/

    /*try {
            //validamos el tipo de conexión de la aplicación
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                    typeConnection=  "privado";//Coenxión por Wifi
                }else{
                    if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                        typeConnection=  "publico"; //Conexión por Datos
                    }
                }
            }else{
                builder = new AlertDialog.Builder(builder.getContext());
                builder.setTitle("Error!!");
                builder.setMessage("El dispositivo no se encuentra conectado a una red, Estado:"+networkInfo.getState());
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
public void cargarZonaTrabajo(){
    //Cargamos las Zonas de Trabajo
    try {
        ArrayList<ZonaTrabajo> listadoZonaTrabajo =null;
        listadoZonaTrabajo = new ControlDB_ZonaTrabajo("publico").buscarZonasTrabajosActivas();
            /*System.out.println("Placa reconocida: " + placa.getText().toString());
            listadoMvtoCarbon = controlDB_Carbon.cargarPlacaTransito(placa.getText().toString());
            if (listadoMvtoCarbon != null) {
                for (MvtoCarbon mvtonCar : listadoMvtoCarbon) {
                    System.out.println(mvtonCar.getArticulo().getDescripcion());
                    placaS.setText("" + mvtonCar.getPlaca());
                    articulo.setText("" + mvtonCar.getArticulo().getDescripcion());
                    cliente.setText("" + mvtonCar.getCliente().getDescripcion());
                    transportadora.setText("" + mvtonCar.getTransportadora().getDescripcion());
                    deposito.setText("" + mvtonCar.getDeposito());
                    peso.setText("" + mvtonCar.getPesoVacio());
                    fechaTara.setText("" + mvtonCar.getFechaEntradaVehiculo());
                    Objeto = mvtonCar;
                }
            }*/
    }catch (Exception e){
        e.printStackTrace();
    }
}

}


