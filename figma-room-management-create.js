// Figma use_figma script for file O3x2x1loAfWPHTdsSlXBJC.
// Creates two missing admin screens:
// - 객실 관리 화면
// - 객실 등록/수정 모달
//
// Run through the Figma MCP `use_figma` tool with:
// skillNames: "figma-use,figma-generate-design"

const createdNodeIds = [];
const fontRegular = { family: 'Inter', style: 'Regular' };
const fontMedium = { family: 'Inter', style: 'Medium' };
const fontSemi = { family: 'Inter', style: 'Semi Bold' };
const fontBold = { family: 'Inter', style: 'Bold' };
await Promise.all([fontRegular, fontMedium, fontSemi, fontBold].map(f => figma.loadFontAsync(f)));

const C = {
  bg: { r: 0.964, g: 0.973, b: 0.984 },
  surface: { r: 1, g: 1, b: 1 },
  line: { r: 0.89, g: 0.91, b: 0.94 },
  text: { r: 0.07, g: 0.09, b: 0.13 },
  muted: { r: 0.39, g: 0.45, b: 0.54 },
  primary: { r: 0.11, g: 0.33, b: 0.78 },
  primarySoft: { r: 0.88, g: 0.93, b: 1 },
  green: { r: 0.05, g: 0.55, b: 0.29 },
  greenSoft: { r: 0.87, g: 0.97, b: 0.91 },
  red: { r: 0.82, g: 0.18, b: 0.18 },
  redSoft: { r: 1, g: 0.91, b: 0.91 },
  amber: { r: 0.73, g: 0.42, b: 0.04 },
  amberSoft: { r: 1, g: 0.95, b: 0.82 },
  violet: { r: 0.43, g: 0.24, b: 0.83 },
  violetSoft: { r: 0.94, g: 0.90, b: 1 },
};

function solid(color, opacity = 1) {
  return [{ type: 'SOLID', color, opacity }];
}

function text(name, value, size, font, color = C.text, width = null) {
  const t = figma.createText();
  t.name = name;
  t.fontName = font;
  t.fontSize = size;
  t.lineHeight = { unit: 'AUTO' };
  t.fills = solid(color);
  t.characters = value;
  if (width) {
    t.textAutoResize = 'HEIGHT';
    t.resize(width, t.height);
  } else {
    t.textAutoResize = 'WIDTH_AND_HEIGHT';
  }
  createdNodeIds.push(t.id);
  return t;
}

function auto(name, dir = 'VERTICAL', gap = 0, pad = [0, 0, 0, 0]) {
  const f = figma.createAutoLayout(dir, { name, itemSpacing: gap });
  f.paddingTop = pad[0];
  f.paddingRight = pad[1];
  f.paddingBottom = pad[2];
  f.paddingLeft = pad[3];
  createdNodeIds.push(f.id);
  return f;
}

function card(name, w, h = null) {
  const f = auto(name, 'VERTICAL', 0, [20, 20, 20, 20]);
  f.fills = solid(C.surface);
  f.strokes = solid(C.line);
  f.strokeWeight = 1;
  f.cornerRadius = 12;
  if (h) f.resize(w, h);
  else f.resize(w, 80);
  f.layoutSizingHorizontal = 'FIXED';
  f.primaryAxisSizingMode = h ? 'FIXED' : 'AUTO';
  f.counterAxisSizingMode = 'FIXED';
  return f;
}

function pill(label, fg, bg) {
  const p = auto(`Badge / ${label}`, 'HORIZONTAL', 6, [5, 10, 5, 10]);
  p.fills = solid(bg);
  p.cornerRadius = 999;
  p.counterAxisAlignItems = 'CENTER';
  const dot = figma.createEllipse();
  dot.name = 'Status Dot';
  dot.resize(6, 6);
  dot.fills = solid(fg);
  p.appendChild(dot);
  createdNodeIds.push(dot.id);
  p.appendChild(text('Badge Label', label, 12, fontMedium, fg));
  return p;
}

function button(label, kind = 'primary') {
  const b = auto(`Button / ${label}`, 'HORIZONTAL', 8, [10, 16, 10, 16]);
  b.cornerRadius = 8;
  b.counterAxisAlignItems = 'CENTER';
  b.fills = solid(kind === 'primary' ? C.primary : C.surface);
  b.strokes = kind === 'primary' ? [] : solid(C.line);
  b.appendChild(text('Button Label', label, 14, fontSemi, kind === 'primary' ? C.surface : C.text));
  return b;
}

function field(label, value, w) {
  const wrap = auto(`Field / ${label}`, 'VERTICAL', 6, [0, 0, 0, 0]);
  wrap.resize(w, 70);
  wrap.counterAxisSizingMode = 'FIXED';
  wrap.appendChild(text('Field Label', label, 13, fontMedium, C.muted));
  const input = auto('Input', 'HORIZONTAL', 0, [11, 12, 11, 12]);
  input.resize(w, 40);
  input.counterAxisSizingMode = 'FIXED';
  input.fills = solid(C.surface);
  input.strokes = solid(C.line);
  input.cornerRadius = 8;
  input.appendChild(text('Input Value', value, 14, fontRegular, C.text));
  wrap.appendChild(input);
  return wrap;
}

function sidebar(active) {
  const s = auto('Admin Sidebar', 'VERTICAL', 8, [24, 16, 24, 16]);
  s.resize(240, 1008);
  s.fills = solid({ r: 0.055, g: 0.075, b: 0.11 });
  s.counterAxisSizingMode = 'FIXED';
  s.primaryAxisSizingMode = 'FIXED';

  const logo = auto('Sidebar Logo', 'HORIZONTAL', 10, [0, 0, 28, 0]);
  logo.counterAxisAlignItems = 'CENTER';
  const mark = figma.createRectangle();
  mark.name = 'Logo Mark';
  mark.resize(32, 32);
  mark.cornerRadius = 8;
  mark.fills = solid(C.primary);
  createdNodeIds.push(mark.id);
  logo.appendChild(mark);
  logo.appendChild(text('Logo Text', 'StayNow Admin', 18, fontBold, C.surface));
  s.appendChild(logo);

  ['대시보드', '예약 관리', '객실 관리', '요금 설정', '매출 분석', '프로모션', '리뷰 관리', '회원 관리'].forEach(item => {
    const nav = auto(`Nav / ${item}`, 'HORIZONTAL', 10, [11, 14, 11, 14]);
    nav.resize(208, 42);
    nav.cornerRadius = 8;
    nav.counterAxisSizingMode = 'FIXED';
    nav.counterAxisAlignItems = 'CENTER';
    nav.fills = item === active ? solid(C.primary) : [];
    const bullet = figma.createRectangle();
    bullet.name = 'Nav Icon Placeholder';
    bullet.resize(16, 16);
    bullet.cornerRadius = 4;
    bullet.fills = solid(item === active ? C.surface : { r: 0.47, g: 0.54, b: 0.64 });
    createdNodeIds.push(bullet.id);
    nav.appendChild(bullet);
    nav.appendChild(text('Nav Label', item, 14, fontMedium, item === active ? C.surface : { r: 0.74, g: 0.78, b: 0.84 }));
    s.appendChild(nav);
  });
  return s;
}

function stat(label, value, sub, accent, bg) {
  const c = card(`Stat / ${label}`, 214, 112);
  c.itemSpacing = 10;
  const top = auto('Stat Top', 'HORIZONTAL', 8, [0, 0, 0, 0]);
  top.resize(174, 24);
  top.counterAxisSizingMode = 'FIXED';
  top.counterAxisAlignItems = 'CENTER';
  const ic = figma.createRectangle();
  ic.name = 'Stat Icon';
  ic.resize(24, 24);
  ic.cornerRadius = 7;
  ic.fills = solid(bg);
  createdNodeIds.push(ic.id);
  top.appendChild(ic);
  top.appendChild(text('Stat Label', label, 13, fontMedium, C.muted));
  c.appendChild(top);
  c.appendChild(text('Stat Value', value, 26, fontBold, C.text));
  c.appendChild(text('Stat Sub', sub, 12, fontRegular, accent));
  return c;
}

let maxX = 0;
for (const child of figma.currentPage.children) maxX = Math.max(maxX, child.x + child.width);

const screen = figma.createAutoLayout('HORIZONTAL', { name: '객실 관리 화면', itemSpacing: 0 });
screen.resize(1440, 1008);
screen.x = maxX + 220;
screen.y = -2680;
screen.fills = solid(C.bg);
screen.primaryAxisSizingMode = 'FIXED';
screen.counterAxisSizingMode = 'FIXED';
createdNodeIds.push(screen.id);
figma.currentPage.appendChild(screen);

screen.appendChild(sidebar('객실 관리'));
const main = auto('Room Management Content', 'VERTICAL', 22, [32, 32, 32, 32]);
main.resize(1200, 1008);
main.counterAxisSizingMode = 'FIXED';
main.primaryAxisSizingMode = 'FIXED';
main.fills = solid(C.bg);
screen.appendChild(main);

const header = auto('Page Header', 'HORIZONTAL', 16, [0, 0, 0, 0]);
header.resize(1136, 48);
header.counterAxisSizingMode = 'FIXED';
header.primaryAxisAlignItems = 'SPACE_BETWEEN';
header.counterAxisAlignItems = 'CENTER';
const hcopy = auto('Header Copy', 'VERTICAL', 4, [0, 0, 0, 0]);
hcopy.appendChild(text('Title', '객실 관리', 28, fontBold, C.text));
hcopy.appendChild(text('Subtitle', '객실 타입, 판매 상태, 청소/점검 상태를 한 화면에서 관리합니다.', 14, fontRegular, C.muted));
header.appendChild(hcopy);
const actions = auto('Header Actions', 'HORIZONTAL', 10, [0, 0, 0, 0]);
actions.appendChild(button('객실 일괄 등록', 'secondary'));
actions.appendChild(button('+ 객실 추가', 'primary'));
header.appendChild(actions);
main.appendChild(header);

const stats = auto('Stats Row', 'HORIZONTAL', 14, [0, 0, 0, 0]);
stats.resize(1136, 112);
stats.counterAxisSizingMode = 'FIXED';
stats.appendChild(stat('전체 객실', '128', '전월 대비 +6실', C.primary, C.primarySoft));
stats.appendChild(stat('판매 가능', '94', '현재 예약 가능', C.green, C.greenSoft));
stats.appendChild(stat('투숙 중', '27', '오늘 체크아웃 8건', C.violet, C.violetSoft));
stats.appendChild(stat('청소/점검', '7', '즉시 처리 필요', C.amber, C.amberSoft));
stats.appendChild(stat('판매 중지', '3', '관리자 확인 필요', C.red, C.redSoft));
main.appendChild(stats);

const toolbar = card('Room Toolbar', 1136, 72);
toolbar.layoutMode = 'HORIZONTAL';
toolbar.itemSpacing = 12;
toolbar.counterAxisAlignItems = 'CENTER';
toolbar.primaryAxisAlignItems = 'SPACE_BETWEEN';
const filters = auto('Filters', 'HORIZONTAL', 10, [0, 0, 0, 0]);
filters.appendChild(field('검색', '객실명, 객실번호 검색', 260));
filters.appendChild(field('객실 타입', '전체', 150));
filters.appendChild(field('판매 상태', '전체 상태', 150));
filters.appendChild(field('층', '전체 층', 120));
toolbar.appendChild(filters);
const toolBtns = auto('Toolbar Buttons', 'HORIZONTAL', 8, [0, 0, 0, 0]);
toolBtns.appendChild(button('필터 초기화', 'secondary'));
toolBtns.appendChild(button('엑셀 다운로드', 'secondary'));
toolbar.appendChild(toolBtns);
main.appendChild(toolbar);

const table = card('Room Table', 1136, 548);
table.paddingTop = 0;
table.paddingRight = 0;
table.paddingBottom = 0;
table.paddingLeft = 0;
const cols = [110, 170, 120, 120, 120, 150, 140, 110, 96];

function row(name, values, isHeader = false) {
  const r = auto(name, 'HORIZONTAL', 0, [0, 0, 0, 0]);
  r.resize(1136, isHeader ? 48 : 64);
  r.counterAxisSizingMode = 'FIXED';
  r.primaryAxisSizingMode = 'FIXED';
  r.fills = isHeader ? solid({ r: 0.98, g: 0.985, b: 0.992 }) : solid(C.surface);
  values.forEach((v, i) => {
    const cell = auto(`Cell ${i + 1}`, 'HORIZONTAL', 0, [0, 16, 0, 16]);
    cell.resize(cols[i], isHeader ? 48 : 64);
    cell.counterAxisSizingMode = 'FIXED';
    cell.primaryAxisSizingMode = 'FIXED';
    cell.counterAxisAlignItems = 'CENTER';
    if (typeof v === 'object' && v.badge) cell.appendChild(pill(v.badge, v.fg, v.bg));
    else cell.appendChild(text('Cell Text', String(v), isHeader ? 13 : 14, isHeader ? fontSemi : fontRegular, isHeader ? C.muted : C.text, cols[i] - 32));
    r.appendChild(cell);
  });
  return r;
}

table.appendChild(row('Table Header', ['객실번호', '객실명', '타입', '기준/최대', '기본가', '판매상태', '청소상태', '오늘 일정', '관리'], true));
[
  ['201', '디럭스 더블 201', '디럭스', '2 / 3', '₩198,000', { badge: '판매 가능', fg: C.green, bg: C.greenSoft }, { badge: '청소 완료', fg: C.green, bg: C.greenSoft }, '예약 없음', '수정'],
  ['205', '디럭스 더블 205', '디럭스', '2 / 3', '₩198,000', { badge: '투숙 중', fg: C.violet, bg: C.violetSoft }, { badge: '입실 완료', fg: C.violet, bg: C.violetSoft }, '15:00 체크인', '상세'],
  ['301', '프리미어 트윈 301', '프리미어', '2 / 4', '₩248,000', { badge: '판매 가능', fg: C.green, bg: C.greenSoft }, { badge: '청소 중', fg: C.amber, bg: C.amberSoft }, '18:00 체크인', '배정'],
  ['401', '스위트룸 401', '스위트', '2 / 4', '₩428,000', { badge: '예약 마감', fg: C.primary, bg: C.primarySoft }, { badge: '점검 필요', fg: C.red, bg: C.redSoft }, '연박 투숙', '점검'],
  ['502', '패밀리룸 502', '패밀리', '4 / 6', '₩368,000', { badge: '판매 가능', fg: C.green, bg: C.greenSoft }, { badge: '청소 완료', fg: C.green, bg: C.greenSoft }, '예약 없음', '수정'],
  ['110', '스탠다드 110', '스탠다드', '2 / 2', '₩128,000', { badge: '판매 중지', fg: C.red, bg: C.redSoft }, { badge: '수리중', fg: C.red, bg: C.redSoft }, '판매중지', '해제'],
].forEach((vals, idx) => table.appendChild(row(`Room Row ${idx + 1}`, vals)));
main.appendChild(table);

const bottom = auto('Bottom Panels', 'HORIZONTAL', 16, [0, 0, 0, 0]);
bottom.resize(1136, 166);
bottom.counterAxisSizingMode = 'FIXED';
const housekeeping = card('Housekeeping Queue', 560, 166);
housekeeping.itemSpacing = 14;
housekeeping.appendChild(text('Panel Title', '하우스키핑 요청', 17, fontBold, C.text));
[['301', '청소 진행중', '30분 경과'], ['401', '욕실 점검 필요', '시설팀 배정'], ['110', '에어컨 수리', '판매중지']].forEach(x => {
  const item = auto(`HK ${x[0]}`, 'HORIZONTAL', 8, [0, 0, 0, 0]);
  item.resize(520, 24);
  item.primaryAxisAlignItems = 'SPACE_BETWEEN';
  item.counterAxisAlignItems = 'CENTER';
  item.appendChild(text('HK Text', `${x[0]}호 · ${x[1]}`, 14, fontMedium, C.text));
  item.appendChild(text('HK Meta', x[2], 13, fontRegular, C.muted));
  housekeeping.appendChild(item);
});
const allocation = card('Room Allocation Preview', 560, 166);
allocation.itemSpacing = 14;
allocation.appendChild(text('Panel Title', '오늘 객실 배정 대기', 17, fontBold, C.text));
[['RES-2024078', '김민준', '디럭스 더블', '미배정'], ['RES-2024079', '오하나', '프리미어 트윈', '301호 추천'], ['RES-2024080', '박현우', '패밀리룸', '502호 추천']].forEach(x => {
  const item = auto(`Alloc ${x[0]}`, 'HORIZONTAL', 8, [0, 0, 0, 0]);
  item.resize(520, 24);
  item.primaryAxisAlignItems = 'SPACE_BETWEEN';
  item.counterAxisAlignItems = 'CENTER';
  item.appendChild(text('Alloc Text', `${x[0]} · ${x[1]} · ${x[2]}`, 14, fontMedium, C.text));
  item.appendChild(text('Alloc Meta', x[3], 13, fontRegular, x[3] === '미배정' ? C.red : C.green));
  allocation.appendChild(item);
});
bottom.appendChild(housekeeping);
bottom.appendChild(allocation);
main.appendChild(bottom);

const overlay = figma.createFrame();
overlay.name = '객실 등록/수정 모달';
overlay.resize(1440, 1008);
overlay.x = screen.x + 1480;
overlay.y = -2680;
overlay.fills = [{ type: 'SOLID', color: { r: 0.02, g: 0.025, b: 0.035 }, opacity: 0.42 }];
createdNodeIds.push(overlay.id);
figma.currentPage.appendChild(overlay);

const modal = auto('Room Edit Dialog', 'VERTICAL', 0, [0, 0, 0, 0]);
modal.resize(720, 760);
modal.x = 360;
modal.y = 110;
modal.fills = solid(C.surface);
modal.cornerRadius = 16;
modal.clipsContent = true;
modal.counterAxisSizingMode = 'FIXED';
modal.primaryAxisSizingMode = 'FIXED';
overlay.appendChild(modal);

const mhead = auto('Dialog Header', 'HORIZONTAL', 0, [24, 28, 20, 28]);
mhead.resize(720, 88);
mhead.primaryAxisAlignItems = 'SPACE_BETWEEN';
mhead.counterAxisSizingMode = 'FIXED';
mhead.counterAxisAlignItems = 'CENTER';
const mhcopy = auto('Dialog Copy', 'VERTICAL', 4, [0, 0, 0, 0]);
mhcopy.appendChild(text('Dialog Title', '객실 정보 등록/수정', 22, fontBold, C.text));
mhcopy.appendChild(text('Dialog Subtitle', '판매 상태와 객실 운영 정보를 함께 관리합니다.', 14, fontRegular, C.muted));
mhead.appendChild(mhcopy);
mhead.appendChild(text('Close', '×', 24, fontRegular, C.muted));
modal.appendChild(mhead);

const body = auto('Dialog Body', 'VERTICAL', 18, [24, 28, 24, 28]);
body.resize(720, 584);
body.counterAxisSizingMode = 'FIXED';
body.fills = solid({ r: 0.985, g: 0.989, b: 0.995 });
const row1 = auto('Form Row 1', 'HORIZONTAL', 14, [0, 0, 0, 0]);
row1.appendChild(field('객실번호', '301', 206));
row1.appendChild(field('객실명', '프리미어 트윈 301', 430));
body.appendChild(row1);
const row2 = auto('Form Row 2', 'HORIZONTAL', 14, [0, 0, 0, 0]);
row2.appendChild(field('객실 타입', '프리미어 트윈', 206));
row2.appendChild(field('층 / 동', '3층 / A동', 206));
row2.appendChild(field('기준 / 최대 인원', '2명 / 4명', 206));
body.appendChild(row2);
const row3 = auto('Form Row 3', 'HORIZONTAL', 14, [0, 0, 0, 0]);
row3.appendChild(field('기본 요금', '₩248,000', 206));
row3.appendChild(field('주말 요금', '₩298,000', 206));
row3.appendChild(field('성수기 요금', '₩348,000', 206));
body.appendChild(row3);
const row4 = auto('Form Row 4', 'HORIZONTAL', 14, [0, 0, 0, 0]);
row4.appendChild(field('판매 상태', '판매 가능', 206));
row4.appendChild(field('청소 상태', '청소 중', 206));
row4.appendChild(field('점검 상태', '정상', 206));
body.appendChild(row4);
const amen = auto('Amenity Chips', 'VERTICAL', 10, [0, 0, 0, 0]);
amen.resize(650, 92);
amen.counterAxisSizingMode = 'FIXED';
amen.appendChild(text('Field Label', '객실 편의시설', 13, fontMedium, C.muted));
const chips = auto('Chips', 'HORIZONTAL', 8, [0, 0, 0, 0]);
['무료 Wi-Fi', '넷플릭스', '욕조', '시티뷰', '조식 가능', '금연'].forEach((c, i) => chips.appendChild(pill(c, i < 4 ? C.primary : C.muted, i < 4 ? C.primarySoft : { r: 0.94, g: 0.95, b: 0.97 })));
amen.appendChild(chips);
body.appendChild(amen);
body.appendChild(field('관리 메모', '침구 교체 완료. 욕실 어메니티 보충 필요.', 650));
modal.appendChild(body);

const foot = auto('Dialog Footer', 'HORIZONTAL', 10, [20, 28, 20, 28]);
foot.resize(720, 88);
foot.counterAxisSizingMode = 'FIXED';
foot.primaryAxisAlignItems = 'SPACE_BETWEEN';
foot.counterAxisAlignItems = 'CENTER';
foot.appendChild(button('객실 판매 중지', 'secondary'));
const right = auto('Footer Right', 'HORIZONTAL', 10, [0, 0, 0, 0]);
right.appendChild(button('취소', 'secondary'));
right.appendChild(button('저장하기', 'primary'));
foot.appendChild(right);
modal.appendChild(foot);

figma.viewport.scrollAndZoomIntoView([screen, overlay]);
return {
  success: true,
  createdNodeIds,
  screens: [
    { name: screen.name, id: screen.id },
    { name: overlay.name, id: overlay.id },
  ],
};
