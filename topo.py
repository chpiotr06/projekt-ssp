from mininet.topo import Topo

class MyTopo( Topo ):
  def __init__( self ):
    Topo.__init__( self )

    # Edge switch
    edgeSw = self.addSwitch('s1')

    # Super spine swiches
    superSpineSw1 = self.addSwitch('s2')
    superSpineSw2 = self.addSwitch('s3')

    # connect superspinse to edge
    self.addLink(edgeSw, superSpineSw1)
    self.addLink(edgeSw, superSpineSw2)

    # Spine switches
    spineSw1 = self.addSwitch('s4')
    spineSw2 = self.addSwitch('s5')
    spineSw3 = self.addSwitch('s6')    

    # connect spineSw1 to superSpines
    self.addLink(spineSw1, superSpineSw1)
    self.addLink(spineSw1, superSpineSw2)

    # connect spineSw2 to superSpines
    self.addLink(spineSw2, superSpineSw1)
    self.addLink(spineSw2, superSpineSw2)

    # connect spineSw3 to superSpines
    self.addLink(spineSw3, superSpineSw1)
    self.addLink(spineSw3, superSpineSw2)

    # Servers
    server1 = self.addHost('server1')
    server2 = self.addHost('server2')
    server3 = self.addHost('server3')
    server4 = self.addHost('server4')
    server5 = self.addHost('server5')

    # connect server1 to spines
    self.addLink(server1, spineSw1)
    self.addLink(server1, spineSw2)

    # connect server2 to spines
    self.addLink(server2, spineSw1)
    self.addLink(server2, spineSw2)

    # connect server3 to spines
    self.addLink(server3, spineSw1)
    self.addLink(server3, spineSw3)

    # connect server3 to spines
    self.addLink(server4, spineSw2)
    self.addLink(server4, spineSw3)

    # connect server3 to spines
    self.addLink(server5, spineSw2)
    self.addLink(server5, spineSw3)

    # Clients
    client1 = self.addHost('client1')
    client2 = self.addHost('client2')
    client3 = self.addHost('client3')
    client4 = self.addHost('client4')
    client5 = self.addHost('client5')

    # connect clients to edge switch
    self.addLink(client1, edgeSw)
    self.addLink(client2, edgeSw)
    self.addLink(client3, edgeSw)
    self.addLink(client4, edgeSw)
    self.addLink(client5, edgeSw)

topos = { 'mytopo': ( lambda: MyTopo() ) }