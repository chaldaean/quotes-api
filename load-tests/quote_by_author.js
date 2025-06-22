import http from 'k6/http';
import { check } from 'k6';

const AUTHOR = encodeURIComponent(__ENV.AUTHOR || 'Lauren Bacall');
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    scenarios: {
        search_by_author: {
            executor: 'constant-arrival-rate',
            rate: 50,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,
            maxVUs: 100,
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<200'],
    },
};

export default function () {
    const res = http.get(`${BASE_URL}/quotes?author=${AUTHOR}`);
    check(res, { 'status 200': (r) => r.status === 200 });
}
