package ro.ppoo.banking.repository;
import ro.ppoo.banking.model.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private final List<Client> clients = new ArrayList<>();
    private final String FILE_PATH = "data/clients.dat";

    public Client findByCNP(String CNP){
        for (Client client : clients){
            if(client.getCNP().equals(CNP)){
                return new Client(client);
            }
        }

        return null;
    }

    public List<Client> getAll(){
        List<Client> copy = new ArrayList<>();

        for(Client client : clients){
            copy.add(new Client(client));
        }
        return copy;
    }

    public void add(Client client){
        clients.add(client);
    }

    public void update(Client updatedClient) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getCNP().equals(updatedClient.getCNP())) {

                Client originalClient = clients.get(i);
                originalClient.setFirstname(updatedClient.getFirstname());
                originalClient.setLastname(updatedClient.getLastname());
                originalClient.setEmail(updatedClient.getEmail());
                originalClient.setPhone(updatedClient.getPhone());
                originalClient.setAccounts(updatedClient.getAccounts());
                originalClient.setPassword(updatedClient.getPassword());
                return;
            }
        }
    }

    public void destroy(Client client) {
        clients.removeIf(c -> c.getCNP().equals(client.getCNP()));
    }


    public void saveToFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(clients);
            System.out.println(FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromFile(){
        File file = new File(FILE_PATH);

        if(!file.exists()|| file.length() == 0){
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Client> loadClients = (List<Client>) ois.readObject();
            clients.clear();
            clients.addAll(loadClients);
            System.out.println("Loaded " + clients.size() + " clients");
        } catch (EOFException e) {
            System.err.println("Empty file");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error at loading clients file");
        }
    }
}
