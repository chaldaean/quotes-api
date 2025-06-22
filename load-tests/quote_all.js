import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    scenarios: {
        get_all_quotes: {
            executor: 'per-vu-iterations',
            vus: 10, // 10 virtual users
            iterations: 5,      // total of 10 requests
            maxDuration: '180s', // safety cap
        },
    },
    thresholds: {
        http_req_duration: ['max<30000'],   // must finish in < 30 000 ms
    },
};

export default function () {
    const res = http.get(`${BASE_URL}/quotes`);
    check(res, {
        'status was 200': (r) => r.status === 200,
    });
}
