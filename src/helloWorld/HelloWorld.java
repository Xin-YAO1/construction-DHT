package helloWorld;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class HelloWorld implements EDProtocol {
    
    //identifiant de la couche transport
    private int transportPid;

    //objet couche transport
    private HWTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;
    
    //initialiser les voisins
    private Node vd;
	private Node vg;
	
	//identifiant de noeud
	private int nId;

    public HelloWorld(String prefix) {
    	this.prefix = prefix;
    	//initialisation des identifiants a partir du fichier de configuration
    	this.transportPid = Configuration.getPid(prefix + ".transport");
    	this.mypid = Configuration.getPid(prefix + ".myself");
    	this.transport = null;
    	this.vd = null;
    	this.vg = null;
    }

    //methode appelee lorsqu'un message est recu par le protocole HelloWorld du noeud
    public void processEvent( Node node, int pid, Object event ) {
    	this.receive((Message)event);
    }
    
    //methode necessaire pour la creation du reseau (qui se fait par clonage d'un prototype)
    public Object clone() {

    	HelloWorld dolly = new HelloWorld(this.prefix);

    	return dolly;
    }

    //liaison entre un objet de la couche applicative et un 
    //objet de la couche transport situes sur le meme noeud
    public void setTransportLayer(int nodeId) {
    	this.nodeId = nodeId;
    	this.transport = (HWTransport) Network.get(this.nodeId).getProtocol(this.transportPid);
    }

    //envoi d'un message (l'envoi se fait via la couche transport)
    public void send(Message msg, Node dest) {
    	this.transport.send(getMyNode(), dest, msg, this.mypid);
    }
    
    

    //affichage a la reception
    private void receive(Message msg) {
    	switch (msg.getType()) {
    	
    	case 0:
    		//System.out.println(this);
        	System.out.println(this +"(iden:"+ this.getNid()+")"+ ": Received " + msg.getContent());
        	System.out.println(this + "(iden:"+ this.getNid()+")"+": Send to "+ this.getVd().getProtocol(this.mypid));
        	System.out.println(" ");
        	if(this.getId()!= 0) {
        		this.send(msg, this.getVd());
        	}
        	break;
        	
    	case 1:
    		System.out.println("Received join");
        	System.out.println(this +"(iden:"+ this.getNid()+")"+ ": Received " + msg.getContent());
        	System.out.println(this + "(iden:"+ this.getNid()+")"+": Send to "+ this.getVd().getProtocol(this.mypid));
        	System.out.println(" ");
        	break;
    	
    	case 2:
    		System.out.println("Received leave");
        	System.out.println(this +"(iden:"+ this.getNid()+")"+ ": Received " + msg.getContent());
        	System.out.println(this + "(iden:"+ this.getNid()+")"+": Send to "+ this.getVd().getProtocol(this.mypid));
        	System.out.println(" ");
        	this.deliver();
        	break;
    	case 3:
    		System.out.print(this +" is notified: "+msg.getContent());
    		System.out.println(" ");
    	}
    	
    }

    //retourne le noeud courant
    private Node getMyNode() {
	return Network.get(this.nodeId);
    }

    public String toString() {
	return "Node "+ this.nodeId;
    }
    
    public int getId() {
		return this.nodeId;
	}
    
    public void setVg(Node n) {
		this.vg=n;
	}

	public void setVd(Node n) {
		this.vd=n;
	}
	
	public Node getVg() {
		return this.vg;
	}
	public Node getVd() {
		return this.vd;
	}
	
	public int getNid() {
		return nId;
	}
	
	public void setNid(int nId) {
		this.nId = nId;
	}
	
	public void ajouter(int nodeId) {
		//Puisqu'un nœud ne peut être ajouté que s'il n'existe pas dans l'anneau
		// ce nœud ne peut être ajouté qu'entre le nœud 0 et le plus grand nœud du cercle.
		System.out.println(this+"(iden:"+ this.getNid()+")"  + " va s'inserer dans le cercle");
		System.out.println("Ce noeud va notifier le Node 0 et le Node " +  Integer.toString(nodeId-1)+" que il va ajouter");
		
		Node nodeG;
		Node nodeD;
		Node node;
		HelloWorld currentG;
		HelloWorld currentD;
		
		nodeG = Network.get(0);
		nodeD = Network.get(nodeId-1);
		node = Network.get(nodeId);
		
		currentG = (HelloWorld)nodeG.getProtocol(this.mypid);
		currentD = (HelloWorld)nodeD.getProtocol(this.mypid);
		currentG.setVg(node);
		currentD.setVd(node);
		Message joinMsg = new Message(Message.JOIN,"i joined ");
		// envoyer join message a nouveau voisins
		this.send(joinMsg, Network.get(0));
		this.send(joinMsg, nodeD);
		// mettre les voisins
		this.setVd(Network.get(0));
		this.setVg(nodeD);
	}
	
	public void laisser(int nodeId) {
		System.out.println(this+"(iden:"+ this.getNid()+")" + " va sortir le cercle");
		Node nodeG = null;
		Node nodeD = null;
		HelloWorld currentG;
		HelloWorld currentD;
		
		int nodeNb = Network.size();
		// si le noeud sorti est le dernier noeud du cercle, par example nbNode est 5 et le id de noeud sorti est 4
		// alors le voisin droite de noeud3 est noeud0
		if (nodeId == nodeNb-1) {
			nodeG = Network.get(nodeId-1);
			nodeD = Network.get(0);
		}
		// si le noeud sorti est noeud0
		else if(nodeId == 0) {
			nodeG = Network.get(nodeNb-1);
			nodeD = Network.get(nodeId+1);
		}
		else {
			nodeG = Network.get(nodeId-1);
			nodeD = Network.get(nodeId+1);
		}		
	
		currentG = (HelloWorld)nodeG.getProtocol(this.mypid);
		currentD = (HelloWorld)nodeD.getProtocol(this.mypid);
		currentG.setVd(nodeD);
		currentD.setVg(nodeG);
		
		Message leaveMsg = new Message(Message.LEAVE,"i left");
		this.send(leaveMsg, nodeG);
		//this.send(leaveMsg, nodeD);
		//mettre voisins null
		this.setVg(null);
		this.setVd(null);
	}
	
	// si il y a un noeud recu les message de 'leave'
	//il va deliver cette information a tous les noeud dans ce cercle
	public void deliver() {
		Message byeMsg = new Message(Message.BYE,"there is a noeud left our cercle");
		int nodeNb = Network.size();
		for(int i=0; i<nodeNb; i++) {
			this.send(byeMsg,Network.get(i));
		}
		
		
	}



    
}