import { check } from 'k6'; 
import http from 'k6/http';

export let options = { 
  vus: 50,
  duration: '3s'
};

const genRandom = () => Math.floor(Math.random() * 3)

export default function() {
  const routes = [
  '1m.txt',
  '5m.txt',
  '10m.txt',
  ]
  
  let res = http.get(`http://10.0.0.2/${routes[genRandom()]}`); check(res, {'is status 200': (r) => r.status === 200});
}
