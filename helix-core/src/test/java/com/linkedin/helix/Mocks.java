package com.linkedin.helix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import com.linkedin.helix.healthcheck.HealthReportProvider;
import com.linkedin.helix.healthcheck.ParticipantHealthReportCollector;
import com.linkedin.helix.messaging.AsyncCallback;
import com.linkedin.helix.messaging.handling.HelixTaskExecutor;
import com.linkedin.helix.messaging.handling.HelixTaskResult;
import com.linkedin.helix.messaging.handling.MessageHandlerFactory;
import com.linkedin.helix.model.Message;
import com.linkedin.helix.participant.StateMachineEngine;
import com.linkedin.helix.participant.statemachine.StateModel;
import com.linkedin.helix.participant.statemachine.StateModelInfo;
import com.linkedin.helix.participant.statemachine.Transition;
import com.linkedin.helix.store.PropertyStore;

public class Mocks
{
  public static class MockStateModel extends StateModel
  {
    boolean stateModelInvoked = false;

    public void onBecomeMasterFromSlave(Message msg, NotificationContext context)
    {
      stateModelInvoked = true;
    }

    public void onBecomeSlaveFromOffline(Message msg, NotificationContext context)
    {
      stateModelInvoked = true;
    }
  }

  @StateModelInfo(states = "{'OFFLINE','SLAVE','MASTER'}", initialState = "OFFINE")
  public static class MockStateModelAnnotated extends StateModel
  {
    boolean stateModelInvoked = false;

    @Transition(from = "SLAVE", to = "MASTER")
    public void slaveToMaster(Message msg, NotificationContext context)
    {
      stateModelInvoked = true;
    }

    @Transition(from = "OFFLINE", to = "SLAVE")
    public void offlineToSlave(Message msg, NotificationContext context)
    {
      stateModelInvoked = true;
    }
  }

  public static class MockHelixTaskExecutor extends HelixTaskExecutor
  {
    boolean completionInvoked = false;

    @Override
    protected void reportCompletion(String msgId)
    {
      System.out.println("Mocks.MockCMTaskExecutor.reportCompletion()");
      completionInvoked = true;
    }

    public boolean isDone(String msgId)
    {
      Future<HelixTaskResult> future = _taskMap.get(msgId);
      if (future != null)
      {
        return future.isDone();
      }
      return false;
    }
  }

  public static class MockManager implements HelixManager
  {
    MockAccessor accessor;

    private final String _clusterName;
    private final String _sessionId;
    String _instanceName;
    ClusterMessagingService _msgSvc;
    private String _version;

    public MockManager()
    {
      this("testCluster-" + Math.random() * 10000 % 999);
    }

    public MockManager(String clusterName)
    {
      _clusterName = clusterName;
      accessor = new MockAccessor(clusterName);
      _sessionId = UUID.randomUUID().toString();
      _instanceName = "testInstanceName";
      _msgSvc = new MockClusterMessagingService();
    }

    @Override
    public void disconnect()
    {

    }

    @Override
    public void addIdealStateChangeListener(IdealStateChangeListener listener) throws Exception
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addLiveInstanceChangeListener(LiveInstanceChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addConfigChangeListener(ConfigChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addMessageListener(MessageListener listener, String instanceName)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addCurrentStateChangeListener(CurrentStateChangeListener listener,
        String instanceName, String sessionId)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addExternalViewChangeListener(ExternalViewChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public DataAccessor getDataAccessor()
    {
      return accessor;
    }

    @Override
    public String getClusterName()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getInstanceName()
    {
      return _instanceName;
    }

    @Override
    public void connect()
    {
      // TODO Auto-generated method stub

    }

    @Override
    public String getSessionId()
    {
      return _sessionId;
    }

    @Override
    public boolean isConnected()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public long getLastNotificationTime()
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void addControllerListener(ControllerChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean removeListener(Object listener)
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public HelixAdmin getClusterManagmentTool()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public ClusterMessagingService getMessagingService()
    {
      // TODO Auto-generated method stub
      return _msgSvc;
    }

    @Override
    public ParticipantHealthReportCollector getHealthReportCollector()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public InstanceType getInstanceType()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public PropertyStore<ZNRecord> getPropertyStore()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getVersion()
    {
      return _version;
    }

    public void setVersion(String version)
    {
      _version = version;
    }

    @Override
    public void addHealthStateChangeListener(HealthStateChangeListener listener, String instanceName)
        throws Exception
    {
      // TODO Auto-generated method stub

    }

    @Override
    public StateMachineEngine getStateMachineEngine()
    {
      // TODO Auto-generated method stub
      return null;
    }

  }

  public static class MockAccessor implements DataAccessor
  {
    private final String _clusterName;
    Map<String, ZNRecord> data = new HashMap<String, ZNRecord>();

    public MockAccessor()
    {
      this("testCluster-" + Math.random() * 10000 % 999);
    }

    public MockAccessor(String clusterName)
    {
      _clusterName = clusterName;
    }

    Map<String, ZNRecord> map = new HashMap<String, ZNRecord>();

    @Override
    public boolean setProperty(PropertyType type, ZNRecordDecorator value, String... keys)
    {
      return setProperty(type, value.getRecord(), keys);
    }

    @Override
    public boolean setProperty(PropertyType type, ZNRecord value, String... keys)
    {
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      data.put(path, value);
      return true;
    }

    @Override
    public boolean updateProperty(PropertyType type, ZNRecordDecorator value, String... keys)
    {
      return updateProperty(type, value.getRecord(), keys);
    }

    @Override
    public boolean updateProperty(PropertyType type, ZNRecord value, String... keys)
    {
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      if (type.updateOnlyOnExists)
      {
        if (data.containsKey(path))
        {
          if (type.mergeOnUpdate)
          {
            ZNRecord znRecord = new ZNRecord(data.get(path));
            znRecord.merge(value);
            data.put(path, znRecord);
          } else
          {
            data.put(path, value);
          }
        }
      } else
      {
        if (type.mergeOnUpdate)
        {
          if (data.containsKey(path))
          {
            ZNRecord znRecord = new ZNRecord(data.get(path));
            znRecord.merge(value);
            data.put(path, znRecord);
          } else
          {
            data.put(path, value);
          }
        } else
        {
          data.put(path, value);
        }
      }

      return true;
    }

    @Override
    public <T extends ZNRecordDecorator> T getProperty(Class<T> clazz, PropertyType type,
        String... keys)
    {
      ZNRecord record = getProperty(type, keys);
      if (record == null)
      {
        return null;
      }
      return ZNRecordDecorator.convertToTypedInstance(clazz, record);
    }

    @Override
    public ZNRecord getProperty(PropertyType type, String... keys)
    {
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      return data.get(path);
    }

    @Override
    public boolean removeProperty(PropertyType type, String... keys)
    {
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      data.remove(path);
      return true;
    }

    @Override
    public List<String> getChildNames(PropertyType type, String... keys)
    {
      List<String> child = new ArrayList<String>();
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      for (String key : data.keySet())
      {
        if (key.startsWith(path))
        {
          String[] keySplit = key.split("\\/");
          String[] pathSplit = path.split("\\/");
          if (keySplit.length > pathSplit.length)
          {
            child.add(keySplit[pathSplit.length + 1]);
          }
        }
      }
      return child;
    }

    @Override
    public <T extends ZNRecordDecorator> List<T> getChildValues(Class<T> clazz, PropertyType type,
        String... keys)
    {
      List<ZNRecord> list = getChildValues(type, keys);
      return ZNRecordDecorator.convertToTypedList(clazz, list);
    }

    @Override
    public List<ZNRecord> getChildValues(PropertyType type, String... keys)
    {
      List<ZNRecord> childs = new ArrayList<ZNRecord>();
      String path = PropertyPathConfig.getPath(type, _clusterName, keys);
      for (String key : data.keySet())
      {
        if (key.startsWith(path))
        {
          String[] keySplit = key.split("\\/");
          String[] pathSplit = path.split("\\/");
          if (keySplit.length - pathSplit.length == 1)
          {
            ZNRecord record = data.get(key);
            if (record != null)
            {
              childs.add(record);
            }
          } else
          {
            System.out.println("keySplit:" + Arrays.toString(keySplit));
            System.out.println("pathSplit:" + Arrays.toString(pathSplit));
          }
        }
      }
      return childs;
    }

    @Override
    public <T extends ZNRecordDecorator> Map<String, T> getChildValuesMap(Class<T> clazz,
        PropertyType type, String... keys)
    {
      List<T> list = getChildValues(clazz, type, keys);
      return ZNRecordDecorator.convertListToMap(list);
    }
  }

  public static class MockHealthReportProvider extends HealthReportProvider
  {

    @Override
    public Map<String, String> getRecentHealthReport()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void resetStats()
    {
      // TODO Auto-generated method stub

    }

  }

  public static class MockClusterMessagingService implements ClusterMessagingService
  {

    @Override
    public int send(Criteria recipientCriteria, Message message)
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int send(Criteria receipientCriteria, Message message, AsyncCallback callbackOnReply,
        int timeOut)
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int sendAndWait(Criteria receipientCriteria, Message message,
        AsyncCallback callbackOnReply, int timeOut)
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void registerMessageHandlerFactory(String type, MessageHandlerFactory factory)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public int send(Criteria receipientCriteria, Message message, AsyncCallback callbackOnReply,
        int timeOut, int retryCount)
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int sendAndWait(Criteria receipientCriteria, Message message,
        AsyncCallback callbackOnReply, int timeOut, int retryCount)
    {
      // TODO Auto-generated method stub
      return 0;
    }

  }
}