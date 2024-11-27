Polecenie do włączenia topologii `sudo mn --custom fulltopo.py --topo mytopo`

## Topologia
![topo](https://github.com/user-attachments/assets/bb9d925d-ab4f-427c-b51a-bdf9278d06e1)

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
