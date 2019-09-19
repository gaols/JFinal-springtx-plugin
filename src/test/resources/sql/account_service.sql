#namespace("account")
  #sql("update_balance_by_name")
  UPDATE `Account`
  SET `balance` = #para(b)
  WHERE `name` in(#for(name : names)#(for.index == 0 ? "" : ",") #para(name)#end)
  #end
#end
