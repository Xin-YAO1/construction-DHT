package helloWorld;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    
    private int helloWorldPid;

    public Initializer(String prefix) {
    	//recuperation du pid de la couche applicative
    	this.helloWorldPid = Configuration.getPid(prefix + ".helloWorldProtocolPid");
    }

    public boolean execute() {
    	int nodeNb;
    	int min = 0;
    	int max = 999;
    	HelloWorld emitter, current,currentNoeud,joinNodeCouche;
    	Node dest, noeud, traiteeNoeud;
    	Message helloMsg,joinMsg ;

    	//recuperation de la taille du reseau
    	nodeNb = Network.size();
    	//nodeNb = 3;
    	// creation du message 
    	helloMsg = new Message(Message.HELLOWORLD,"Hello!!");
    	joinMsg = new Message(Message.JOIN,"i joined ");
    	if (nodeNb < 1) {
    		System.err.println("Network size is not positive");
    		System.exit(1);
    	}

    	//recuperation de la couche applicative de l'emetteur (le noeud 0)
    	emitter = (HelloWorld)Network.get(0).getProtocol(this.helloWorldPid);
    	emitter.setTransportLayer(0);

    	//pour chaque noeud, on fait le lien entre la couche applicative et la couche transport
    	//puis on fait envoyer au noeud 0 un message "Hello"
    	for (int i = 1; i < nodeNb; i++) {
    		dest = Network.get(i);
    		current = (HelloWorld)dest.getProtocol(this.helloWorldPid);
    		current.setTransportLayer(i);
    		//distribuer chaque noeud un identifiant entre 0 et 999
    		current.setNid(min+(int)(Math.random()*(max-min+1)));
    	}
    	//attribuer les voisins pour chaque noeud
    	for ( int i=0 ; i<nodeNb; i++) {
    		noeud = Network.get(i);
    		currentNoeud = (HelloWorld)noeud.getProtocol(this.helloWorldPid);
    		for (int j=0; j<nodeNb; j++) {
    			//voisin
    			dest = Network.get(j);
    			current = (HelloWorld)dest.getProtocol(this.helloWorldPid);
    			//attribution des voisins
    			if (current.getId()==currentNoeud.getId()-1 || (current.getId()==nodeNb-1 && currentNoeud.getId()==0)) {
    				currentNoeud.setVg(dest);
    				current.setVd(noeud);
    			}
    		}
    	}
	
	// on initialise node3 va etre ajouter ou supprimer dans le cercle
		int nodeId = 3;
		traiteeNoeud = Network.get(nodeId);
		current = (HelloWorld)traiteeNoeud.getProtocol(this.helloWorldPid);
		
		// si ce noeud deja dans le cercle, on le supprime
		if(nodeId < nodeNb) {
			current.laisser(nodeId);
		}
		// si ce noeud n'est pas dans le cercle, on le ajoute
		else {
			current.setTransportLayer(nodeId);
			// distribuer node3 un identifiant
			current.setNid(min+(int)(Math.random()*(max-min+1)));
			current.ajouter(nodeId);
		}
	
		emitter.send(helloMsg, emitter.getVd());
		System.out.println("Initialization completed");
		return false;
    }
}