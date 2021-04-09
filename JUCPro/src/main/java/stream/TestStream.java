package stream;

import java.util.Arrays;
import java.util.List;

public class TestStream {
	public static void main(String[] args) {
		/**
		 * 题目要求： 用一行代码实现
		 * 1. Id 必须是偶数
		 * 2. 年龄必须大于23
		 * 3. 用户名转为大写
		 * 4. 用户名倒序
		 * 5. 只能输出一个用户
		 */
		User u1 = new User(1, "a", 23);
        User u2 = new User(2, "b", 23);
        User u3 = new User(3, "c", 23);
        User u4 = new User(6, "d", 24);
        User u5 = new User(4, "e", 25);
        // 存储 交给集合
        List<User> list = Arrays.asList(u1,u2,u3,u4,u5);
        
        // 计算 交给流
        list.stream().filter( (user)->{ return user.getId()%2 == 0; } )
        			 .filter( (user)->{ return user.getAge() > 23; } )
        			 .map( (user)->{ return user.getName().toUpperCase(); } )
        			 .sorted( (user1, user2)->{ return user2.compareTo(user1); })
        			 .limit(1)
        			 .forEach((user)->{System.out.println(user);});
	}
}
