import { check } from 'k6'; 
import http from 'k6/http';

export let options = { 
  vus: 7,
  duration: '120s'
};

export default function() {
  let res = http.get('http://10.0.0.100'); check(res, {'is status 200': (r) => r.status === 200});
}