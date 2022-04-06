package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor    //final로 선언된 객체의 생성자를 자동으로 생성해준다.(밑의 주석내용)
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired          //생성자가 하나일 경우 생략가능
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }


    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

//    @PostMapping("/add")    //상품등록 페이지를 가져올땐 GET, 상품등록버튼을 눌렀을때는 POST방식처리
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {                         //html의 name값으로 들어온다.
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

//        model.addAttribute("item", item);

        return "basic/item";
    }

//    @PostMapping("/add")    //기존 저장방식을 ModelAttribute활용하여 변환
    public String addItemV2(@ModelAttribute("item") Item item) {    //model 제거 => 자동추가 된다.
//        Item item = new Item();               //ModelAttribute가 처리해주는 내용들
//        item.setItemName(itemName);           //Item객체에 setter를 호출해서 값을 set해준다.
//        item.setPrice(price);
//        item.setQuantity(quantity);

        itemRepository.save(item);
//        model.addAttribute("item", item);     //@ModelAttribute("item")이 수행하는 역할 //자동추가됨.
        return "basic/item";
    }

//    @PostMapping("/add")    //기존 저장방식을 ModelAttribute활용하여 변환
    public String addItemV3(@ModelAttribute Item item) { //value()를 생략하게되면 객채의 첫글자를 소문자로 만들어 추가됨. Item -> item

        itemRepository.save(item);
//        model.addAttribute("item", item);     //@ModelAttribute("item")이 수행하는 역할 //자동추가됨.
        return "basic/item";
    }
//    @PostMapping("/add")
    public String addItemV4(Item item) { //ModelAttribute의 생략   //String같은 단순타입 ->@RequestParam , 객채 -> ModelAttribute
        itemRepository.save(item);
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV5(Item item){
            itemRepository.save(item);                          //redirect => 요청을 받아 리턴할때 redirect를 사용하면 해당값으로 다시 요청한다.
            return "redirect:/basic/items/"+item.getId();       //새로고침을 하는경우 post방식은 url을 계속타면서 save(item)을 무한 실행함. 때문에 redirect로 페이지를 돌려주는 방법
                                                                //PRG방식 => POST -> Redirect -> GET
        }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes){
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());      //리턴값의 {itemId}와 매핑된다.
        redirectAttributes.addAttribute("status", true);      //매핑할 값이 없는경우는 쿼리파라미터형식 ?staus=true형식으로 들어가게된다.
        return "redirect:/basic/items/{itemId}";                                       //http://localhost:8080/basic/items/3?status=true
        //PRG방식 => POST -> Redirect -> GET
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }


    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {

        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
