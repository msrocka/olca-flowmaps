import math
import uuid

import olca
import fedelemflowlist


def _uid(*args) -> str:
    """ A helper function that creates a name based UUID. """
    path = '/'.join([str(arg).strip() for arg in args]).lower()
    return str(uuid.uuid3(uuid.NAMESPACE_OID, path))


def _isnil(val) -> bool:
    if val is None:
        return True
    if isinstance(val, float):
        return math.isnan(val)
    if isinstance(val, str):
        return val.strip() == ""
    return False


def _isnum(val) -> bool:
    if isinstance(val, (float, int)):
        return not math.isnan(val)
    return False


def _path(*args) -> str:
    p = ''
    for arg in args:
        if _isnil(arg):
            continue
        if p != '':
            p = p + "/"
        p = p + str(arg)
    return p


def _s(val) -> str:
    if _isnil(val):
        return None
    return str(val).strip()


class MapFlow(object):

    def __init__(self):
        self.name = None      # type: str
        self.uid = None       # type: str
        self.category = None  # type: str
        self.property = None  # type: str
        self.unit = None      # type: str


class MapEntry(object):
    """ Describes a mapping entry in the Fed.LCA flow list. """

    def __init__(self, row):

        # source flow attributes
        self.source_list = _s(row['SourceListName'])

        self.source_flow = MapFlow()
        self.source_flow.name = _s(row['SourceFlowName'])
        self.source_flow.uid = _s(row['SourceFlowUUID'])
        self.source_flow.category = _path(row['SourceFlowCategory1'],
                                          row['SourceFlowCategory2'],
                                          row['SourceFlowCategory3'])
        self.source_flow.property = _s(row['SourceProperty'])
        self.source_flow.unit = _s(row['SourceUnit'])

        # traget flow attributs
        self.target_flow = MapFlow()
        self.target_flow.name = _s(row['TargetFlowName'])
        self.target_flow.uid = _s(row['TargetFlowUUID'])
        self.target_flow.category = _path(row['TargetFlowCategory1'],
                                          row['TargetFlowCategory2'],
                                          row['TargetFlowCategory3'])
        self.target_flow.property = _s(row['TargetProperty'])
        self.target_flow.unit = _s(row['TargetUnit'])

        factor = row['ConversionFactor']
        if _isnum(factor):
            self.factor = factor
        else:
            self.factor = 1.0

    def to_json(self) -> dict:


def main():
    # returns a pandas DataFrame
    flow_map = fedelemflowlist.get_flowmapping(version='0.1')

    maps = {}
    for i, row in flow_map.iterrows():
        me = MapEntry(row)
        m = maps.get(me.source_list)
        if m is None:
            m = []
            maps[me.source_list] = m
        m.append(me)

    for source_list, entries in maps.items():
        list_ref = olca.Ref()
        list_ref.olca_type = 'FlowMap'
        list_ref.name = source_list
        mappings = []
        mapping = {
            'name': '%s -> Fed.LCA Commons' % source_list,
            'source': list_ref.to_json(),
            'mappings': mappings
        }
        for e in entries:
            mappings.append(e.to_json())
        print(source_list, len(entries))


if __name__ == "__main__":
    main()
