import http from 'k6/http';
import { check } from 'k6';

// --- customise ----------------------------------------------------------
// Either hard-code a valid ObjectId below, or inject with
//  k6 run -e QUOTE_ID=<id> by-id.js
const QUOTE_ID = __ENV.QUOTE_ID || '5eb17aadb69dc744b4e70d35';
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    scenarios: {
        search_by_id: {
            executor: 'constant-arrival-rate',
            rate: 50,              // 50 requests / second
            timeUnit: '1s',
            duration: '30s',       // run for 30s (≈ 1 500 requests total)
            preAllocatedVUs: 50,   // allocate enough VUs to hit the rate
            maxVUs: 100,
        },
    },
    thresholds: {
        http_req_duration: [
            'p(95)<200'   // 95 % under 200 ms
        ],
    },
};

export default function () {
    const res = http.get(`${BASE_URL}/quotes/${QUOTE_ID}`);
    check(res, { 'status 200': (r) => r.status === 200 });
}

