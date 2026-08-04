import assert from "node:assert/strict";
import { existsSync, readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const testDir = dirname(fileURLToPath(import.meta.url));
const staticRoot = resolve(testDir, "../../main/resources/static");
const appSource = readFileSync(resolve(staticRoot, "omnistay/assets/app.js"), "utf8");

const forbiddenUiData = [
  "seedPage",
  "seed.html",
  "그랜드 서울",
  "부산 오션 리조트",
  "예약 조회 고객",
  "장기 투숙 고객",
  "lookup@omnistay.test",
  "vip@omnistay.test",
  "김하나",
  "이도윤",
  "박서연",
  "function legacy",
  "토스페이먼츠 샘플",
  "userId=1",
  "getCurrentUser()?.userId || 1"
];

for (const value of forbiddenUiData) {
  assert.equal(appSource.includes(value), false, `운영 UI에 하드코딩 데이터가 남아 있습니다: ${value}`);
}

assert.equal(existsSync(resolve(staticRoot, "seed.html")), false, "샘플 데이터 생성 화면이 남아 있습니다.");

for (const apiPath of ["/api/hotels", "/api/bookings/", "/api/payment"]) {
  assert.equal(appSource.includes(apiPath), true, `DB 연동 경로가 유지되어야 합니다: ${apiPath}`);
}

console.log("STATIC_UI_HARDCODED_DATA_AUDIT_OK");
