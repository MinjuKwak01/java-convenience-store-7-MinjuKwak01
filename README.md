### 입력

- 구매할 상품명과 수량을 입력받는다.
  (수량은 하이픈(-)으로, 개별 상품은 대괄호([])로 묶어 쉼표(,)로 구분한다.)
  - 구매 수량이 재고 수량을 초과한 경우 예외가 발생한다.
  - 존재하지 않는 상품을 입력한 경우 예외가 발생한다.
  - 입력 형식이 올바르지 않은 경우 예외가 발생한다.
  - 기타 잘못된 입력의 경우 예외가 발생한다.
- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 추가 여부를 입력받는다.
  - Y: 증정 받을 수 있는 상품을 추가한다.
  - N: 증정 받을 수 있는 상품을 추가하지 않는다.
    - Y 나 N이 아닌 경우 예외가 발생한다.
- 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부를 입력받는다.
  - Y: 일부 수량에 대해 정가로 결제한다.
  - N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
    - Y 나 N이 아닌 경우 예외가 발생한다.
- 멤버십 할인 적용 여부를 입력 받는다.
  - Y: 멤버십 할인을 적용한다.
  - N: 멤버십 할인을 적용하지 않는다.
    - Y 나 N이 아닌 경우 예외가 발생한다.
- 추가 구매 여부를 입력 받는다.
  - Y: 재고가 업데이트된 상품 목록을 확인 후 추가로 구매를 진행한다.
  - N: 구매를 종료한다.
    - Y 나 N이 아닌 경우 예외가 발생한다.

### 편의점

- [promotions.md](http://promotion.md) 파일을 읽어 저장하는 기능
  - 이름, 필요한 개수, 무료 개수, 시작날짜, 끝나는 날짜
- [products.md](http://products.md) 파일을 읽어 저장하는 기능
  - 이름, 가격, 양, 프로모션 이름
- **입력받은 상품이 프로모션중인 상품인 경우**
  - **프로모션 상품의 재고가 입력한 개수보다 작은 경우**
    ex) 2+1인 콜라 7개 / 입력 : [콜라-9]
    - 일부 수량에 대해 정가로 결제할지 여부 : Yes
      - 최대 구매 가능한 프로모션 개수만큼 프로모션 상품 재고에서 차감
      - 나머지 재고는 일반 상품 재고에서 차감
    - 일부 수량에 대해 정가로 결제할지 여부 : No
      - 최대 구매 가능한 프로모션 개수만큼 프로모션 상품 재고에서 차감
  - **프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우**
    ex) 2+1인 콜라 7개 / 입력 : [콜라-5]
    - 수량만큼 추가 여부 : Yes
      - 입력 수량 + 무료 수량 만큼 프로모션 상품 재고에서 차감
    - 수량만큼 추가 여부 : No
      - 입력 수량만큼  프로모션 상품 재고에서 차감
  - **프로모션 적용이 가능한 상품에 대해 고객이 알맞게 가져온 경우**
    ex) 2+1인 콜라 7개 / 입력 : [콜라-6]
    - 입력 수량만큼  프로모션 상품 재고에서 차감
- **입력 받은 상품이 일반 상품인 경우**
  - 일반 상품에서 재고 차감
- 멤버십 할인 여부 : Yes
  - 프로모션 미적용 금액의 30% 를 계산하여 할인 (멤버십 할인의 최대 한도는 8,000원)
- 멤버십 할인 여부 : No
  - 별도의 할인 없이 진행
- **영수증 출력 기능**
  - 상품별 구매 수량 계산
  - 상품별 구매 금액 계산
  - 최종 할인 금액 계산
  - 멤버십 할인 금액 계산

### 출력

- 파일 입출력을 활용해서 불러오고, 환영 인사와 함께 상품명, 가격, 프로모션 이름, 재고를 출력한다,
  - 재고가 0개라면 “재고 없음”을 출력한다.
- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 혜택에 대한 안내 메시지를 출력한다.
- 멤버십 할인 적용 여부를 확인하기 위해 안내 문구를 출력한다.
- 구매 상품 내역, 증정 상품 내역, 금액 정보를 출력한다.
- 추가 구매 여부를 확인하기 위해 안내 문구를 출력한다.