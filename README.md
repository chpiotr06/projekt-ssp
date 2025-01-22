Polecenie do włączenia topologii `sudo mn --custom fulltopo.py --topo mytopo`

## Cel i zakres

- Stworzenie Load Balancera Least Bandwidth
- Kontrolowanie obciążenia serwerów
- Dynamiczne równoważenie obciążenia – przekierowywanie ruchu do serwera, który przetwarza najmniej danych w momencie wysyłania zapytania
- 5 klientów
- 5 serwerów
- Wybrany sterownik: floodlight

## Topologia

![topology](https://raw.githubusercontent.com/chpiotr06/projekt-ssp/refs/heads/main/image.png)

## Użyte narzędzia

- Sterownik Floodlight
- Mininet
- Generator obciążenia K6
- Simple Http Server
- Dowolny emulator teminala pozwalajacy na X11 forwarding (np. putty)

## Instalacja potrzebnego oprogramowania

### K6

Dokumentacja: https://grafana.com/docs/k6/latest/set-up/install-k6/

### Floodlight + Mininet

1. Pobieramy obraz dyskowy maszyny z linku: http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-vm.zip
2. Tworzymy nową maszynę wirtualna w VirtualBox. Jako obraz dysku ustawiamy pobrany plik.

## Uruchomienie

### Sterownik

Aby zaczać pracę ze sterownikiem należy otworzyc z terminala połączenie SSH do maszyny wirtualnej, a nastepnie wpisac polecenie eclipse. Jesli X11 jest poprawnie uruchimiony to otworzy się okno eclipse.

#### Import projektu do środowiska Eclipse IDE

Proszę uruchomić środowisko Eclipse i w razie potrzeby utworzyć nowe Workspace. Proszę także
usunąć stare projekty (o ile istnieją). W celu importu projektu proszę wybrać kolejno:

- File -> Import -> General -> Existing Projects into Workspace
- Kliknąć na przycisk Browse obok Select root directory i odnaleźć katalog, do którego został
  pobrany wcześniej projekt floodlightProject
- W oknie Projects zaznaczyć Floodlight
- Kliknąć przycisk Finish.
  Po tym procesie cały projekt powinien zostać zaimportowany. W lewej części środowiska Eclipse
  w oknie Package Explorer powinno pojawić się drzewo katalogów projektu Floodlight.

#### Budowanie i uruchamianie projektu w środowsku Eclipse

Aby zbudować projekt (np. po zmianach w kodzie źródłowym) należy utworzyć cel:

- Wybrać Run->External Tools->External Tools Configurations
- Wybrać Ant->New Configuration
- W polu nazwy wpisać FloodlightLaunch
- W polu Buildfile wybrać plik build.xml
- W polu Base directory wybrać floodlight
- W zakładce Targets wybrać run
- Kliknąć przycisk Apply

### Uruchomienie środowiska mininet

W nowym terminalu z połączeniem SSH do maszyny wirtualnej uruchamiamy poniższą komendę.

```
sudo mn --custom topo1.py --topo mytopo --controller=remote,ip=127.0.0.1,port=6653
```

Pozwala ona na uruchomienie wybranej topologii.
Gdy Mininet zostanie uruchomiony to wpisujemy:

```
xterm client1 client2 client3 client4 client5 server1 server2 server3 server4 server5 s1
```

### Uruchamianie serwera http na kazdym serwerowym xterm

```bash
python3 -m http.server 80
```

### Uruchomienie generatora na każdym klienckim xterm

```bash
k6 run test.js
```

### Sprawdzenie tablicy przepływów xterm przełącznika

```bash
ovs-ofctl dump-flows s1
```

## Opis algorytmu

Klienci (10.0.0.1 – 10.0.0.5) generują zapytania HTTP przy pomocy generatora na adres 10.0.0.100 (Reverse Proxy)
Z serwerów pobierana jest informacja na temat ilości danych przesyłanych do każdego serwera
Kontroler sprawdza który serwer jest najmniej obciążony i na który zostanie przekierowany ruch
Wiedząc, który serwer został wybrany, w pakiecie podmieniany jest docelowy adres IP oraz MAC na adresy odpowiedniego serwera (10.0.0.6 – 10.0.0.10), a następnie pakiet zostaje wysłany na odpowiedni port
Przy powrocie pakietu, docelowy adres IP i MAC są podmieniane na adresy źródłowe tych pakietów
