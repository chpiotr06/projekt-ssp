Polecenie do włączenia topologii `sudo mn --custom fulltopo.py --topo mytopo`

## Topologia
![topology](https://github.com/user-attachments/assets/bb9d925d-ab4f-427c-b51a-bdf9278d06e1)

## Uruchomienie generatora
Na h1: 
```bash
k6 run test.js
```

Na h2 uruchamiamy serwer http:
```bash
python3 -m http.server 80
```

W przypadku braku obslugi xterm uruchamiamy minineta, wpisujemy polecenie dump i w dwoch osobnych terminalach urchmiamy polecenie:
```bash
sudo mnexec -a <pid> bash
```
Pid to odpowiedni numer procesu pochodzący z polecenia dump

## Opis algorytmu
Przełącznik S1 otrzymuje pakiet od dowolnego klienta i wysyła go do S2. S2 przesyła pakiet dalej - do przełącznika S4. Stamtąd pakiet trafia do Serwera 1. Przełącznik S1 otrzymuje kolejny pakiet. Jeśli obciążenie Serwera 1 jest większe niż obciążenie Serwera 2, to pakiet przechodzi tą samą ścieżką i na końcu trafia do Serwera 2. Przełącznik po otrzymaniu kolejnego pakietu porówna obciążenia wszystkich serwerów i prześle pakiet do trzeciego serwera. Kolejne pakiety zostaną przesłane kolejno do czwartego i piątego serwera - po porównaniu na nich obciążenia. Kolejny nadesłany pakiet zostanie przekierowany do tego serwera, który jest najmniej obciążony.
Za każdym razem - przy wyborze do którego serwera ma trafić pakiet - algorytm działa w ten sam sposób. Porównywane są obciążenia serwerów każdy z każdym i następuje przekierowanie pakietu do najmniej obciążonego serwera. 
