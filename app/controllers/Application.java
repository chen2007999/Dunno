package controllers;
import models.*;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Client currentClient;

    // index to landing page by default
    public static Result index() {
        return redirect(routes.Application.landing());
    }

    public static Result landing() {
        currentClient = null;
        return ok(landing.render());
    }

    // get the information that the client just typed
    public static Client getClient() {
        play.data.Form<Client> clientForm = play.data.Form.form(Client.class);
        return clientForm.bindFromRequest().get();
    }

    // methods deal with client account
    public static  Result createClient() {

        Client client = getClient();
        if(client.getEmail() == null) return redirect(routes.Application.register("wtffff"));

        if(Client.checkNull(client) || Client.checkEmpty(client)){
            return redirect(routes.Application.register("Empty details"));
        } else if(!client.emailFormat()){
            return redirect(routes.Application.register("Wrong email format"));
        } else if(!Client.checkPasswordConsistency(client)){
            return redirect(routes.Application.register("Passwords do not match"));
        } else if(Client.clientEmailExists(client)){
            return redirect(routes.Application.register("Client with email " + client.email + " already exists"));
        }
        Client.createClient(client);
        currentClient = client;
        return ok(randChat.render(""));
    }

    public static  Result deleteClientFromDB(String email) {
        Client.deleteClient(email);
        return redirect(routes.Application.displayAllClientsFromDB());
    }

    public static  Result displayAllClientsFromDB() {
        return ok(registration.render(Client.find.findList(), ""));
    }

    public static Result logIn() {
        Client client = getClient();
        if(Client.validate(client)) {
            currentClient = Client.findClient(client);
            return ok(randChat.render(""));
        }
        return ok(landing.render());
    }

    public  static Result register(String error) {
        return ok(registration.render(Client.find.findList(), error));
    }

    // Websocket interface
    public static WebSocket<String> wsInterface(){
        return new WebSocket<String>(){

            // called when websocket handshake is done
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out){
                RandChat.start(in, out);
            }
        };
    }

   /* public static Result newChatMessage() {

    }*/




   /* public static WebSocket<String> socket() {
        return new WebSocket<String>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

                // For each event received on the socket,
                in.onMessage(new Callback<String>() {
                    public void invoke(String event) {

                        // Log events to the console
                        System.out.println(event);

                    }
                });

                // When the socket is closed.
                in.onClose(new Callback0() {
                    public void invoke() {

                        System.out.println("Disconnected");

                    }
                });

                // Send a single 'Hello!' message
                out.write("Hello!");

            }

        };
    } */
}
