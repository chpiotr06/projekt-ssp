from mininet.topo import Topo
from mininet.node import Host

class VIPARPHost(Host):
    def config(self, **params):
        r = super(VIPARPHost, self).config(**params)
        # Ustawiamy statyczny ARP: 10.0.0.100 -> 00:11:22:33:44:55
        self.cmd('arp -s 10.0.0.100 00:11:22:33:44:55')
        return r

def int2dpid( dpid ):
        try:
            dpid = hex( dpid )[ 2: ]
            dpid = '0' * ( 16 - len( dpid ) ) + dpid
            return dpid
        except IndexError:
            raise Exception( 'Unable to derive default datapath ID - '
                             'please either specify a dpid or use a '
                             'canonical switch name such as s23.' )

class MyTopo( Topo ):
  def __init__( self ):
    Topo.__init__( self )

    # Edge switch
    edgeSw = self.addSwitch('s1',dpid=int2dpid(1))

    # Super spine switches
    #superSpineSw1 = self.addSwitch('s2',dpid=int2dpid(2))
    #superSpineSw2 = self.addSwitch('s3',dpid=int2dpid(3))

    # Connect edge switch to super spine switches
    #self.addLink(edgeSw, superSpineSw1)
    #self.addLink(edgeSw, superSpineSw2)

    # Servers
    server1 = self.addHost(name='server1',cls=VIPARPHost,ip='10.0.0.6/24',mac='00:00:00:00:00:06')
    server2 = self.addHost(name='server2',cls=VIPARPHost,ip='10.0.0.7/24',mac='00:00:00:00:00:07')
    server3 = self.addHost(name='server3',cls=VIPARPHost,ip='10.0.0.8/24',mac='00:00:00:00:00:08')
    server4 = self.addHost(name='server4',cls=VIPARPHost,ip='10.0.0.9/24',mac='00:00:00:00:00:09')
    server5 = self.addHost(name='server5',cls=VIPARPHost,ip='10.0.0.10/24',mac='00:00:00:00:00:0A')

    # Connect servers 1, 2, 3 to super spine switch 1 (s2)
    self.addLink(server1, edgeSw)
    self.addLink(server2, edgeSw)
    self.addLink(server3, edgeSw)

    # Connect servers 4, 5 to super spine switch 2 (s3)
    self.addLink(server4, edgeSw)
    self.addLink(server5, edgeSw)

    # Clients
    client1 = self.addHost(name='client1',cls=VIPARPHost,ip='10.0.0.1/24',mac='00:00:00:00:00:01')
    client2 = self.addHost(name='client2',cls=VIPARPHost,ip='10.0.0.2/24',mac='00:00:00:00:00:02')
    client3 = self.addHost(name='client3',cls=VIPARPHost,ip='10.0.0.3/24',mac='00:00:00:00:00:03')
    client4 = self.addHost(name='client4',cls=VIPARPHost,ip='10.0.0.4/24',mac='00:00:00:00:00:04')
    client5 = self.addHost(name='client5',cls=VIPARPHost,ip='10.0.0.5/24',mac='00:00:00:00:00:05')

    # Connect clients to edge switch
    self.addLink(client1, edgeSw)
    self.addLink(client2, edgeSw)
    self.addLink(client3, edgeSw)
    self.addLink(client4, edgeSw)
    self.addLink(client5, edgeSw)

topos = { 'mytopo': ( lambda: MyTopo() ) }