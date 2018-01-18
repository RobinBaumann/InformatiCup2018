import h2o
from h2o.estimators.gbm import H2OGradientBoostingEstimator
import data_preparation as prep
import argparse

TRAIN_VEC = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]
RESPONSE = "price"

parser = argparse.ArgumentParser(description='Start h2o model training.')
parser.add_argument('max_epochs', metavar='i', type=int, nargs='+',
                   help='an integer denoting the max. iterations')

if __name__ == "__main__":
    con = prep.create_connection()
    args = parser.parse_args()
    h2o.init()
    gbm_regressor = H2OGradientBoostingEstimator(model_id="Infocup", distribution="gaussian",
                                                 ntrees=10, max_depth=3, min_rows=2, learn_rate=0.2)
    for i in range(0, 3):
        print("Epoch {}/{}".format(i, 3))
        data = prep.prepare_data_2(con, 10)
        h2o_frame = h2o.H2OFrame(data)
        train, test = h2o_frame.split_frame(ratios=[.8])
        gbm_regressor.train(x=TRAIN_VEC, y=RESPONSE, training_frame=train,
                            validation_frame=test)
        print(gbm_regressor.score_history())
