package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

@Slf4j
@Service
public class AccountTCCServiceImpl implements AccountTCCService {
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFreezeMapper accountFreezeMapper;

    @Override
    public void deduct(String userId, int money) {
        //获取事务id
        String xid = RootContext.getXID();
        //判断freeze是否有冻结记录
        AccountFreeze oldFreeze = accountFreezeMapper.selectById(xid);
        if(oldFreeze !=null){
            //我要拒绝业务
            return;
        }
        //1. 扣减可用余额
        accountMapper.deduct(userId,money);
        //2. 冻结金额，事务状态
        AccountFreeze freeze = new AccountFreeze();
        freeze.setUserId(userId);
        freeze.setFreezeMoney(money);
        freeze.setState(AccountFreeze.State.TRY);
        freeze.setXid(xid);
        accountFreezeMapper.insert(freeze);
    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {
        //1. 获取事务id，根据id删除冻结记录
        String xid = ctx.getXid();
        int count = accountFreezeMapper.deleteById(xid);
        return count ==1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        // 查询冻结记录
        String xid = ctx.getXid();
        String userId = ctx.getActionContext("userId").toString();
        AccountFreeze freeze = accountFreezeMapper.selectById(xid);
        //空回滚判断
        if (freeze == null){
            freeze = new AccountFreeze();
            freeze.setUserId(userId);
            freeze.setFreezeMoney(0);
            freeze.setState(AccountFreeze.State.CANCEL);
            freeze.setXid(xid);
            accountFreezeMapper.insert(freeze);
            return true;
        }
        //幂等处理
        if(freeze.getState() == AccountFreeze.State.CANCEL){
            return  true;
        }
        //1 恢复可用余额
        accountMapper.refund(freeze.getUserId(),freeze.getFreezeMoney());
        //2. 将冻结金额清零，把状态改成cancel
        freeze.setFreezeMoney(0);
        freeze.setState(AccountFreeze.State.CANCEL);
        int count = accountFreezeMapper.updateById(freeze);
        return count==1;
    }
}
